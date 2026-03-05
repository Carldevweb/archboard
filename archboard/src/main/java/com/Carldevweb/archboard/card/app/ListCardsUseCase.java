package com.Carldevweb.archboard.card.app;

import com.Carldevweb.archboard.card.domain.Card;
import com.Carldevweb.archboard.card.domain.CardRepository;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCardsUseCase {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final AccessService accessService;

    public ListCardsUseCase(CardRepository cardRepository, ColumnRepository columnRepository, AccessService accessService) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.accessService = accessService;
    }

    public List<Card> execute(Long userId, Long columnId) {
        var column = columnRepository.findById(columnId)
                .orElseThrow(() -> new NotFoundException("Column not found"));

        accessService.requireBoardOwner(userId, column.getBoardId());

        return cardRepository.findByColumnId(columnId);
    }
}