package com.Carldevweb.archboard.card.app;

import com.Carldevweb.archboard.card.domain.Card;
import com.Carldevweb.archboard.card.domain.CardRepository;
import com.Carldevweb.archboard.card.events.CardMovedEvent;
import com.Carldevweb.archboard.column.domain.Column;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.NotFoundException;
import com.Carldevweb.archboard.common.events.DomainEventPublisher;
import jakarta.persistence.EntityManager;
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
class MoveCardUseCaseTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private AccessService accessService;

    @Mock
    private DomainEventPublisher eventPublisher;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private MoveCardUseCase useCase;

    @Test
    void should_move_card_inside_same_column() {
        Long userId = 1L;
        Long boardId = 100L;
        Long columnId = 10L;
        Long cardId = 1000L;

        Card card1 = new Card(cardId, columnId, "Card 1", "desc", 0);
        Card card2 = new Card(1001L, columnId, "Card 2", "desc", 1);
        Card card3 = new Card(1002L, columnId, "Card 3", "desc", 2);

        Column column = new Column(columnId, boardId, "Todo", 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card1));
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(cardRepository.findByColumnId(columnId)).thenReturn(List.of(card1, card2, card3));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card result = useCase.execute(userId, cardId, columnId, 2);

        assertNotNull(result);
        assertEquals(cardId, result.getId());
        assertEquals(columnId, result.getColumnId());
        assertEquals(2, result.getPosition());

        assertEquals(0, card2.getPosition());
        assertEquals(1, card3.getPosition());
        assertEquals(2, card1.getPosition());

        verify(accessService).requireBoardOwner(userId, boardId);
        verify(entityManager, times(2)).flush();
        verify(eventPublisher).publish(any(CardMovedEvent.class));
    }

    @Test
    void should_move_card_across_columns() {
        Long userId = 1L;
        Long boardId = 100L;
        Long fromColumnId = 10L;
        Long toColumnId = 20L;
        Long cardId = 1000L;

        Card movingCard = new Card(cardId, fromColumnId, "Moving card", "desc", 0);
        Card sourceOtherCard = new Card(1001L, fromColumnId, "Source card", "desc", 1);

        Card targetCard1 = new Card(2001L, toColumnId, "Target 1", "desc", 0);
        Card targetCard2 = new Card(2002L, toColumnId, "Target 2", "desc", 1);

        Column fromColumn = new Column(fromColumnId, boardId, "Todo", 0);
        Column toColumn = new Column(toColumnId, boardId, "Done", 1);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(movingCard));
        when(columnRepository.findById(fromColumnId)).thenReturn(Optional.of(fromColumn));
        when(columnRepository.findById(toColumnId)).thenReturn(Optional.of(toColumn));
        when(cardRepository.findByColumnId(fromColumnId)).thenReturn(List.of(movingCard, sourceOtherCard));
        when(cardRepository.findByColumnId(toColumnId)).thenReturn(List.of(targetCard1, targetCard2));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card result = useCase.execute(userId, cardId, toColumnId, 1);

        assertNotNull(result);
        assertEquals(cardId, result.getId());
        assertEquals(toColumnId, result.getColumnId());
        assertEquals(1, result.getPosition());

        assertEquals(0, sourceOtherCard.getPosition());

        assertEquals(0, targetCard1.getPosition());
        assertEquals(1, movingCard.getPosition());
        assertEquals(2, targetCard2.getPosition());

        verify(accessService).requireBoardOwner(userId, boardId);
        verify(entityManager, times(2)).flush();
        verify(eventPublisher).publish(any(CardMovedEvent.class));
    }

    @Test
    void should_throw_when_card_not_found() {
        Long userId = 1L;
        Long cardId = 1000L;
        Long toColumnId = 20L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(userId, cardId, toColumnId, 0)
        );

        assertEquals("Card not found", exception.getMessage());

        verify(cardRepository).findById(cardId);
        verifyNoInteractions(columnRepository, accessService, eventPublisher, entityManager);
    }

    @Test
    void should_throw_when_target_column_is_in_another_board() {
        Long userId = 1L;
        Long fromBoardId = 100L;
        Long toBoardId = 200L;
        Long fromColumnId = 10L;
        Long toColumnId = 20L;
        Long cardId = 1000L;

        Card card = new Card(cardId, fromColumnId, "Moving card", "desc", 0);
        Column fromColumn = new Column(fromColumnId, fromBoardId, "Todo", 0);
        Column toColumn = new Column(toColumnId, toBoardId, "Other board", 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(columnRepository.findById(fromColumnId)).thenReturn(Optional.of(fromColumn));
        when(columnRepository.findById(toColumnId)).thenReturn(Optional.of(toColumn));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(userId, cardId, toColumnId, 0)
        );

        assertEquals("Cannot move card to a column in another board", exception.getMessage());

        verify(accessService).requireBoardOwner(userId, fromBoardId);
        verify(eventPublisher, never()).publish(any());
        verify(entityManager, never()).flush();
    }
}