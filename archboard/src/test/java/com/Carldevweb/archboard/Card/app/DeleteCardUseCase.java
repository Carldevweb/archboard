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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCardUseCaseTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private AccessService accessService;

    @InjectMocks
    private DeleteCardUseCase useCase;

    @Test
    void should_delete_card_and_compact_positions() {
        Long userId = 1L;
        Long boardId = 100L;
        Long columnId = 10L;
        Long deletedCardId = 1001L;

        Card deletedCard = new Card(deletedCardId, columnId, "Card 2", "desc", 1);
        Column column = new Column(columnId, boardId, "Todo", 0);

        Card remainingCard1 = new Card(1000L, columnId, "Card 1", "desc", 0);
        Card remainingCard2 = new Card(1002L, columnId, "Card 3", "desc", 2);

        when(cardRepository.findById(deletedCardId)).thenReturn(Optional.of(deletedCard));
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(cardRepository.findByColumnId(columnId)).thenReturn(List.of(remainingCard1, remainingCard2));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(userId, deletedCardId);

        verify(accessService).requireBoardOwner(userId, boardId);
        verify(cardRepository).delete(deletedCard);
        verify(cardRepository).findByColumnId(columnId);

        assertEquals(0, remainingCard1.getPosition());
        assertEquals(1, remainingCard2.getPosition());

        verify(cardRepository, times(1)).save(remainingCard2);
        verify(cardRepository, never()).save(remainingCard1);
    }

    @Test
    void should_delete_card_without_reordering_when_positions_are_already_compacted() {
        Long userId = 1L;
        Long boardId = 100L;
        Long columnId = 10L;
        Long deletedCardId = 1000L;

        Card deletedCard = new Card(deletedCardId, columnId, "Card 1", "desc", 0);
        Column column = new Column(columnId, boardId, "Todo", 0);

        Card remainingCard1 = new Card(1001L, columnId, "Card 2", "desc", 0);
        Card remainingCard2 = new Card(1002L, columnId, "Card 3", "desc", 1);

        when(cardRepository.findById(deletedCardId)).thenReturn(Optional.of(deletedCard));
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(cardRepository.findByColumnId(columnId)).thenReturn(List.of(remainingCard1, remainingCard2));

        useCase.execute(userId, deletedCardId);

        verify(cardRepository).delete(deletedCard);
        verify(cardRepository).findByColumnId(columnId);
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void should_throw_when_card_not_found() {
        Long userId = 1L;
        Long cardId = 1000L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(userId, cardId)
        );

        assertEquals("Card not found", exception.getMessage());

        verify(cardRepository).findById(cardId);
        verifyNoInteractions(columnRepository, accessService);
        verify(cardRepository, never()).delete(any(Card.class));
    }

    @Test
    void should_throw_when_column_not_found() {
        Long userId = 1L;
        Long cardId = 1000L;
        Long columnId = 10L;

        Card card = new Card(cardId, columnId, "Card", "desc", 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(columnRepository.findById(columnId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(userId, cardId)
        );

        assertEquals("Column not found", exception.getMessage());

        verify(cardRepository).findById(cardId);
        verify(columnRepository).findById(columnId);
        verify(accessService, never()).requireBoardOwner(anyLong(), anyLong());
        verify(cardRepository, never()).delete(any(Card.class));
    }
}