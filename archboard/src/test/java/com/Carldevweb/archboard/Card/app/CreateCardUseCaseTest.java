package com.Carldevweb.archboard.card.app;

import com.Carldevweb.archboard.card.domain.Card;
import com.Carldevweb.archboard.card.domain.CardRepository;
import com.Carldevweb.archboard.column.domain.Column;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCardUseCaseTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private AccessService accessService;

    @InjectMocks
    private CreateCardUseCase useCase;

    @Test
    void should_create_card_with_next_position() {
        Long userId = 1L;
        Long columnId = 10L;
        Long boardId = 100L;

        Column column = new Column(columnId, boardId, "Todo", 0);

        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(cardRepository.findMaxPosition(columnId)).thenReturn(2);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card result = useCase.execute(userId, columnId, "New card", "description");

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(columnId, result.getColumnId());
        assertEquals("New card", result.getTitle());
        assertEquals("description", result.getDescription());
        assertEquals(3, result.getPosition());

        verify(columnRepository).findById(columnId);
        verify(accessService).requireBoardOwner(userId, boardId);
        verify(cardRepository).findMaxPosition(columnId);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void should_throw_when_column_not_found() {
        Long userId = 1L;
        Long columnId = 10L;

        when(columnRepository.findById(columnId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(userId, columnId, "New card", "description")
        );

        assertEquals("Column not found", exception.getMessage());

        verify(columnRepository).findById(columnId);
        verify(accessService, never()).requireBoardOwner(anyLong(), anyLong());
        verify(cardRepository, never()).findMaxPosition(anyLong());
        verify(cardRepository, never()).save(any(Card.class));
    }
}