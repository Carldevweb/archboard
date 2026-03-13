package com.Carldevweb.archboard.card.app;

import com.Carldevweb.archboard.card.domain.Card;
import com.Carldevweb.archboard.card.domain.CardRepository;
import com.Carldevweb.archboard.card.events.CardMovedEvent;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.NotFoundException;
import com.Carldevweb.archboard.common.events.DomainEventPublisher;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MoveCardUseCase {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final AccessService accessService;
    private final DomainEventPublisher eventPublisher;
    private final EntityManager entityManager;

    public MoveCardUseCase(
            CardRepository cardRepository,
            ColumnRepository columnRepository,
            AccessService accessService,
            DomainEventPublisher eventPublisher,
            EntityManager entityManager
    ) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.accessService = accessService;
        this.eventPublisher = eventPublisher;
        this.entityManager = entityManager;
    }

    @Transactional
    public Card execute(Long userId, Long cardId, Long toColumnId, int requestedPosition) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        var fromColumn = columnRepository.findById(card.getColumnId())
                .orElseThrow(() -> new NotFoundException("Column not found"));

        var toColumn = columnRepository.findById(toColumnId)
                .orElseThrow(() -> new NotFoundException("Column not found"));

        accessService.requireBoardOwner(userId, fromColumn.getBoardId());

        if (!fromColumn.getBoardId().equals(toColumn.getBoardId())) {
            throw new IllegalArgumentException("Cannot move card to a column in another board");
        }

        Long fromColumnId = card.getColumnId();

        Card result;
        if (fromColumnId.equals(toColumnId)) {
            result = moveInsideSameColumn(card, requestedPosition);
        } else {
            result = moveAcrossColumns(card, toColumnId, requestedPosition);
        }

        eventPublisher.publish(
                new CardMovedEvent(
                        fromColumn.getBoardId(),
                        card.getId(),
                        fromColumnId,
                        toColumnId
                )
        );

        return result;
    }

    private Card moveInsideSameColumn(Card card, int requestedPosition) {
        Long columnId = card.getColumnId();
        List<Card> cards = new ArrayList<>(cardRepository.findByColumnId(columnId));

        int currentIndex = indexOf(cards, card.getId());
        if (currentIndex == -1) {
            throw new NotFoundException("Card not found in column");
        }

        Card movedCard = cards.remove(currentIndex);
        int targetIndex = clamp(requestedPosition, 0, cards.size());
        cards.add(targetIndex, movedCard);

        // PASS 1 : positions temporaires uniques négatives
        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            c.moveInside(-(i + 1));
            cardRepository.save(c);
        }
        entityManager.flush();

        // PASS 2 : positions finales
        Card updated = null;

        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            c.moveInside(i);
            Card saved = cardRepository.save(c);

            if (saved.getId().equals(card.getId())) {
                updated = saved;
            }
        }
        entityManager.flush();

        return updated != null ? updated : card;
    }

    private Card moveAcrossColumns(Card card, Long toColumnId, int requestedPosition) {
        Long fromColumnId = card.getColumnId();

        List<Card> sourceCards = new ArrayList<>(cardRepository.findByColumnId(fromColumnId));
        int sourceIndex = indexOf(sourceCards, card.getId());
        if (sourceIndex == -1) {
            throw new NotFoundException("Card not found in source column");
        }

        sourceCards.remove(sourceIndex);

        List<Card> targetCards = new ArrayList<>(cardRepository.findByColumnId(toColumnId));
        int targetIndex = clamp(requestedPosition, 0, targetCards.size());

        // PASS 1A : source -> positions temporaires uniques négatives
        for (int i = 0; i < sourceCards.size(); i++) {
            Card c = sourceCards.get(i);
            c.moveInside(-(i + 1));
            cardRepository.save(c);
        }

        // PASS 1B : target -> positions temporaires uniques négatives
        for (int i = 0; i < targetCards.size(); i++) {
            Card c = targetCards.get(i);
            c.moveTo(toColumnId, -(1000 + i + 1));
            cardRepository.save(c);
        }

        // PASS 1C : card déplacée -> colonne cible avec position temporaire unique
        card.moveTo(toColumnId, -999999);
        cardRepository.save(card);

        entityManager.flush();

        // PASS 2A : source -> positions finales compactées
        for (int i = 0; i < sourceCards.size(); i++) {
            Card c = sourceCards.get(i);
            c.moveInside(i);
            cardRepository.save(c);
        }

        // PASS 2B : target final
        targetCards.add(targetIndex, card);

        Card updated = null;

        for (int i = 0; i < targetCards.size(); i++) {
            Card c = targetCards.get(i);
            c.moveTo(toColumnId, i);
            Card saved = cardRepository.save(c);

            if (saved.getId().equals(card.getId())) {
                updated = saved;
            }
        }

        entityManager.flush();

        return updated != null ? updated : card;
    }

    private int indexOf(List<Card> cards, Long cardId) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getId().equals(cardId)) {
                return i;
            }
        }
        return -1;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}