package com.Carldevweb.archboard.column.app;

import com.Carldevweb.archboard.column.domain.Column;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListColumnsUseCase {

    private final ColumnRepository columnRepository;
    private final AccessService accessService;

    public ListColumnsUseCase(ColumnRepository columnRepository, AccessService accessService) {
        this.columnRepository = columnRepository;
        this.accessService = accessService;
    }

    public List<Column> execute(Long userId, Long boardId) {
        accessService.requireBoardOwner(userId, boardId);
        return columnRepository.findByBoardId(boardId);
    }
}