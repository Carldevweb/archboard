package com.Carldevweb.archboard.column.app;

import com.Carldevweb.archboard.column.domain.Column;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeleteColumnUseCase {

    private final ColumnRepository columnRepository;
    private final AccessService accessService;

    public DeleteColumnUseCase(ColumnRepository columnRepository, AccessService accessService) {
        this.columnRepository = columnRepository;
        this.accessService = accessService;
    }

    @Transactional
    public void execute(Long userId, Long columnId) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new NotFoundException("Column not found"));

        accessService.requireBoardOwner(userId, column.getBoardId());

        Long boardId = column.getBoardId();

        columnRepository.delete(column);

        // Compact positions: 0..n-1
        List<Column> remaining = columnRepository.findByBoardId(boardId);
        for (int i = 0; i < remaining.size(); i++) {
            Column c = remaining.get(i);
            if (c.getPosition() != i) {
                c.move(i);
                columnRepository.save(c);
            }
        }
    }
}