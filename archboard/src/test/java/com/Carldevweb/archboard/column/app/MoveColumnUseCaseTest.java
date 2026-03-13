package com.Carldevweb.archboard.column.app;

import com.Carldevweb.archboard.column.domain.Column;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MoveColumnUseCaseTest {

    @Mock
    private UpdateColumnUseCase updateColumnUseCase;

    @InjectMocks
    private MoveColumnUseCase useCase;

    @Test
    void should_delegate_to_update_column_use_case() {
        Long userId = 1L;
        Long columnId = 10L;
        int position = 2;

        Column updatedColumn = new Column(columnId, 100L, "Done", position);

        when(updateColumnUseCase.execute(userId, columnId, null, position))
                .thenReturn(updatedColumn);

        Column result = useCase.execute(userId, columnId, position);

        assertEquals(updatedColumn, result);
        verify(updateColumnUseCase).execute(userId, columnId, null, position);
    }
}