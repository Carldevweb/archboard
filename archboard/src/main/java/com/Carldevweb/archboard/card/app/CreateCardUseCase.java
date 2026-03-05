package com.Carldevweb.archboard.card.app;

import com.Carldevweb.archboard.card.domain.Card;
import com.Carldevweb.archboard.card.domain.CardRepository;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCardUseCase {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final AccessService accessService;

    public CreateCardUseCase(CardRepository cardRepository, ColumnRepository columnRepository, AccessService accessService) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.accessService = accessService;
    }

    @Transactional
    public Card execute(Long userId, Long columnId, String title, String description) {
        var column = columnRepository.findById(columnId)
                .orElseThrow(() -> new NotFoundException("Column not found"));

        accessService.requireBoardOwner(userId, column.getBoardId());

        int nextPos = cardRepository.findMaxPosition(columnId) + 1;

        Card card = new Card(null, columnId, title, description, nextPos);
        return cardRepository.save(card);
    }
}