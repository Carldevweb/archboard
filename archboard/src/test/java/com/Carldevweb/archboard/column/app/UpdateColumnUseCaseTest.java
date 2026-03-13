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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateColumnUseCaseTest {

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private AccessService accessService;

    @InjectMocks
    private UpdateColumnUseCase useCase;

    @Test
    void should_rename_column() {
        Long userId = 1L;
        Long boardId = 100L;
        Long columnId = 10L;

        Column column = new Column(columnId, boardId, "Todo", 0);

        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(columnRepository.save(column)).thenReturn(column);

        Column result = useCase.execute(userId, columnId, "Done", null);

        assertEquals("Done", result.getName());
        assertEquals(0, result.getPosition());

        verify(accessService).requireBoardOwner(userId, boardId);
        verify(columnRepository).save(column);
    }

    @Test
    void should_reorder_column_inside_board() {
        Long userId = 1L;
        Long boardId = 100L;

        Column col1 = new Column(10L, boardId, "Todo", 0);
        Column col2 = new Column(11L, boardId, "Doing", 1);
        Column col3 = new Column(12L, boardId, "Done", 2);

        when(columnRepository.findById(10L)).thenReturn(Optional.of(col1));
        when(columnRepository.findByBoardId(boardId)).thenReturn(List.of(col1, col2, col3));
        when(columnRepository.save(any(Column.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Column result = useCase.execute(userId, 10L, null, 2);

        assertEquals(10L, result.getId());
        assertEquals(2, result.getPosition());
        assertEquals(0, col2.getPosition());
        assertEquals(1, col3.getPosition());

        verify(accessService).requireBoardOwner(userId, boardId);
        verify(columnRepository).findById(10L);
        verify(columnRepository).findByBoardId(boardId);
        verify(columnRepository, times(3)).save(any(Column.class));
    }

    @Test
    void should_not_save_when_nothing_changed() {
        Long userId = 1L;
        Long boardId = 100L;
        Long columnId = 10L;

        Column column = new Column(columnId, boardId, "Todo", 0);

        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));

        Column result = useCase.execute(userId, columnId, "Todo", null);

        assertEquals("Todo", result.getName());
        assertEquals(0, result.getPosition());

        verify(accessService).requireBoardOwner(userId, boardId);
        verify(columnRepository, never()).save(any(Column.class));
    }

    @Test
    void should_throw_when_column_not_found() {
        Long userId = 1L;
        Long columnId = 10L;

        when(columnRepository.findById(columnId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(userId, columnId, "Done", null)
        );

        assertEquals("Column not found", exception.getMessage());

        verify(columnRepository).findById(columnId);
        verifyNoInteractions(accessService);
    }

    @Test
    void should_throw_when_column_not_found_in_board_during_reorder() {
        Long userId = 1L;
        Long boardId = 100L;
        Long columnId = 10L;

        Column target = new Column(columnId, boardId, "Todo", 0);
        Column other = new Column(11L, boardId, "Doing", 1);

        when(columnRepository.findById(columnId)).thenReturn(Optional.of(target));
        when(columnRepository.findByBoardId(boardId)).thenReturn(List.of(other));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(userId, columnId, null, 1)
        );

        assertEquals("Column not found in board", exception.getMessage());

        verify(accessService).requireBoardOwner(userId, boardId);
        verify(columnRepository, never()).save(any(Column.class));
    }
}