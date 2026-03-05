package com.Carldevweb.archboard.card.app;

import com.Carldevweb.archboard.card.domain.Card;
import com.Carldevweb.archboard.card.domain.CardRepository;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeleteCardUseCase {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final AccessService accessService;

    public DeleteCardUseCase(CardRepository cardRepository, ColumnRepository columnRepository, AccessService accessService) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.accessService = accessService;
    }

    @Transactional
    public void execute(Long userId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        var column = columnRepository.findById(card.getColumnId())
                .orElseThrow(() -> new NotFoundException("Column not found"));

        accessService.requireBoardOwner(userId, column.getBoardId());

        Long columnId = card.getColumnId();

        cardRepository.delete(card);

        // Compact positions 0..n-1
        List<Card> remaining = cardRepository.findByColumnId(columnId);
        for (int i = 0; i < remaining.size(); i++) {
            Card c = remaining.get(i);
            if (c.getPosition() != i) {
                c.moveInside(i);
                cardRepository.save(c);
            }
        }
    }
}