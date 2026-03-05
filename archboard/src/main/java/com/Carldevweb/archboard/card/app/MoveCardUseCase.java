package com.Carldevweb.archboard.card.app;

import com.Carldevweb.archboard.card.domain.Card;
import com.Carldevweb.archboard.card.domain.CardRepository;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MoveCardUseCase {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final AccessService accessService;

    public MoveCardUseCase(CardRepository cardRepository, ColumnRepository columnRepository, AccessService accessService) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.accessService = accessService;
    }

    @Transactional
    public Card execute(Long userId, Long cardId, Long toColumnId, int requestedPosition) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        var fromColumn = columnRepository.findById(card.getColumnId())
                .orElseThrow(() -> new NotFoundException("Column not found"));

        var toColumn = columnRepository.findById(toColumnId)
                .orElseThrow(() -> new NotFoundException("Column not found"));

        // Sécurité : user doit posséder le board (même board attendu)
        accessService.requireBoardOwner(userId, fromColumn.getBoardId());

        if (!fromColumn.getBoardId().equals(toColumn.getBoardId())) {
            // Cross-board interdit (logique Trello)
            throw new IllegalArgumentException("Cannot move card to a column in another board");
        }

        Long fromColumnId = card.getColumnId();

        if (fromColumnId.equals(toColumnId)) {
            return moveInsideSameColumn(card, requestedPosition);
        }

        return moveAcrossColumns(card, toColumnId, requestedPosition);
    }

    private Card moveInsideSameColumn(Card card, int requestedPosition) {
        Long columnId = card.getColumnId();
        List<Card> cards = new ArrayList<>(cardRepository.findByColumnId(columnId));

        int currentIndex = indexOf(cards, card.getId());
        if (currentIndex == -1) throw new NotFoundException("Card not found in column");

        Card removed = cards.remove(currentIndex);
        int clamped = clamp(requestedPosition, 0, Math.max(0, cards.size()));
        cards.add(clamped, removed);

        Card updatedTarget = null;

        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            if (c.getPosition() != i) {
                c.moveInside(i);
                Card saved = cardRepository.save(c);
                if (saved.getId().equals(card.getId())) updatedTarget = saved;
            } else if (c.getId().equals(card.getId())) {
                updatedTarget = c;
            }
        }

        return updatedTarget != null ? updatedTarget : card;
    }

    private Card moveAcrossColumns(Card card, Long toColumnId, int requestedPosition) {
        Long fromColumnId = card.getColumnId();

        // 1) retire de la colonne source + compact
        List<Card> fromCards = new ArrayList<>(cardRepository.findByColumnId(fromColumnId));
        int fromIndex = indexOf(fromCards, card.getId());
        if (fromIndex == -1) throw new NotFoundException("Card not found in source column");

        fromCards.remove(fromIndex);

        for (int i = 0; i < fromCards.size(); i++) {
            Card c = fromCards.get(i);
            if (c.getPosition() != i) {
                c.moveInside(i);
                cardRepository.save(c);
            }
        }

        // 2) insère dans la colonne destination
        List<Card> toCards = new ArrayList<>(cardRepository.findByColumnId(toColumnId));
        int clamped = clamp(requestedPosition, 0, Math.max(0, toCards.size()));

        // on met à jour la card déplacée avec nouvelle colonne, position provisoire
        card.moveTo(toColumnId, clamped);

        toCards.add(clamped, card);

        Card updatedTarget = null;

        for (int i = 0; i < toCards.size(); i++) {
            Card c = toCards.get(i);
            // IMPORTANT : colonne destination pour toutes les cartes de cette liste
            if (!c.getColumnId().equals(toColumnId)) {
                c.moveTo(toColumnId, c.getPosition());
            }

            boolean needSave = false;

            if (c.getPosition() != i) {
                if (c.getId().equals(card.getId())) {
                    c.moveTo(toColumnId, i);
                } else {
                    c.moveInside(i);
                }
                needSave = true;
            }

            // la card déplacée doit être sauvegardée au moins une fois (changement columnId)
            if (c.getId().equals(card.getId()) && !needSave) {
                needSave = true;
            }

            if (needSave) {
                Card saved = cardRepository.save(c);
                if (saved.getId().equals(card.getId())) updatedTarget = saved;
            } else if (c.getId().equals(card.getId())) {
                updatedTarget = c;
            }
        }

        return updatedTarget != null ? updatedTarget : card;
    }

    private int indexOf(List<Card> cards, Long cardId) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getId().equals(cardId)) return i;
        }
        return -1;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}