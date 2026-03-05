package com.Carldevweb.archboard.card.app;

import com.Carldevweb.archboard.card.domain.Card;
import com.Carldevweb.archboard.card.domain.CardRepository;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCardUseCase {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final AccessService accessService;

    public UpdateCardUseCase(CardRepository cardRepository, ColumnRepository columnRepository, AccessService accessService) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.accessService = accessService;
    }

    @Transactional
    public Card execute(Long userId, Long cardId, String title, String description) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"));

        var column = columnRepository.findById(card.getColumnId())
                .orElseThrow(() -> new NotFoundException("Column not found"));

        accessService.requireBoardOwner(userId, column.getBoardId());

        boolean changed = false;

        if (title != null && !title.isBlank() && !title.equals(card.getTitle())) {
            card.rename(title);
            changed = true;
        }

        if (description != null && (card.getDescription() == null || !description.equals(card.getDescription()))) {
            card.changeDescription(description);
            changed = true;
        }

        return changed ? cardRepository.save(card) : card;
    }
}