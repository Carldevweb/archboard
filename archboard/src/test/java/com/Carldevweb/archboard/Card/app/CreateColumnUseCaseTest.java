package com.Carldevweb.archboard.column.app;

import com.Carldevweb.archboard.column.domain.Column;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateColumnUseCaseTest {

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private AccessService accessService;

    @InjectMocks
    private CreateColumnUseCase useCase;

    @Test
    void should_create_column_with_next_position() {
        Long userId = 1L;
        Long boardId = 100L;

        when(columnRepository.findMaxPosition(boardId)).thenReturn(2);
        when(columnRepository.save(any(Column.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Column result = useCase.execute(userId, boardId, "Done");

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(boardId, result.getBoardId());
        assertEquals("Done", result.getName());
        assertEquals(3, result.getPosition());

        verify(accessService).requireBoardOwner(userId, boardId);
        verify(columnRepository).findMaxPosition(boardId);
        verify(columnRepository).save(any(Column.class));
    }
}