package com.Carldevweb.archboard.column.app;

import com.Carldevweb.archboard.column.domain.Column;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteColumnUseCaseTest {

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private AccessService accessService;

    @InjectMocks
    private DeleteColumnUseCase useCase;

    @Test
    void should_delete_column_and_compact_positions() {
        Long userId = 1L;
        Long boardId = 100L;
        Long deletedColumnId = 11L;

        Column deletedColumn = new Column(deletedColumnId, boardId, "Doing", 1);
        Column remainingColumn1 = new Column(10L, boardId, "Todo", 0);
        Column remainingColumn2 = new Column(12L, boardId, "Done", 2);

        when(columnRepository.findById(deletedColumnId)).thenReturn(Optional.of(deletedColumn));
        when(columnRepository.findByBoardId(boardId)).thenReturn(List.of(remainingColumn1, remainingColumn2));
        when(columnRepository.save(any(Column.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(userId, deletedColumnId);

        verify(accessService).requireBoardOwner(userId, boardId);
        verify(columnRepository).delete(deletedColumn);
        verify(columnRepository).findByBoardId(boardId);

        assertEquals(0, remainingColumn1.getPosition());
        assertEquals(1, remainingColumn2.getPosition());

        verify(columnRepository, times(1)).save(remainingColumn2);
        verify(columnRepository, never()).save(remainingColumn1);
    }

    @Test
    void should_delete_column_without_reordering_when_positions_are_already_compacted() {
        Long userId = 1L;
        Long boardId = 100L;
        Long deletedColumnId = 10L;

        Column deletedColumn = new Column(deletedColumnId, boardId, "Todo", 0);
        Column remainingColumn1 = new Column(11L, boardId, "Doing", 0);
        Column remainingColumn2 = new Column(12L, boardId, "Done", 1);

        when(columnRepository.findById(deletedColumnId)).thenReturn(Optional.of(deletedColumn));
        when(columnRepository.findByBoardId(boardId)).thenReturn(List.of(remainingColumn1, remainingColumn2));

        useCase.execute(userId, deletedColumnId);

        verify(accessService).requireBoardOwner(userId, boardId);
        verify(columnRepository).delete(deletedColumn);
        verify(columnRepository).findByBoardId(boardId);
        verify(columnRepository, never()).save(any(Column.class));
    }

    @Test
    void should_throw_when_column_not_found() {
        Long userId = 1L;
        Long columnId = 10L;

        when(columnRepository.findById(columnId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(userId, columnId)
        );

        assertEquals("Column not found", exception.getMessage());

        verify(columnRepository).findById(columnId);
        verifyNoInteractions(accessService);
        verify(columnRepository, never()).delete(any(Column.class));
    }
}