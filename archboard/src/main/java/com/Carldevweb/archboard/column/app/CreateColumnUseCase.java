package com.Carldevweb.archboard.column.app;

import com.Carldevweb.archboard.column.domain.Column;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateColumnUseCase {

    private final ColumnRepository columnRepository;
    private final AccessService accessService;

    public CreateColumnUseCase(ColumnRepository columnRepository, AccessService accessService) {
        this.columnRepository = columnRepository;
        this.accessService = accessService;
    }

    @Transactional
    public Column execute(Long userId, Long boardId, String name) {
        accessService.requireBoardOwner(userId, boardId);

        int nextPosition = columnRepository.findMaxPosition(boardId) + 1;

        Column column = new Column(null, boardId, name, nextPosition);
        return columnRepository.save(column);
    }
}