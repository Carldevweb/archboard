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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateCardUseCaseTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private AccessService accessService;

    @InjectMocks
    private UpdateCardUseCase useCase;

    @Test
    void should_update_title_and_description() {
        Long userId = 1L;
        Long boardId = 100L;
        Long columnId = 10L;
        Long cardId = 1000L;

        Card card = new Card(cardId, columnId, "Old title", "Old desc", 0);
        Column column = new Column(columnId, boardId, "Todo", 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(cardRepository.save(card)).thenReturn(card);

        Card result = useCase.execute(userId, cardId, "New title", "New desc");

        assertEquals("New title", result.getTitle());
        assertEquals("New desc", result.getDescription());

        verify(accessService).requireBoardOwner(userId, boardId);
        verify(cardRepository).save(card);
    }

    @Test
    void should_update_only_title() {
        Long userId = 1L;
        Long boardId = 100L;
        Long columnId = 10L;
        Long cardId = 1000L;

        Card card = new Card(cardId, columnId, "Old title", "Old desc", 0);
        Column column = new Column(columnId, boardId, "Todo", 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(cardRepository.save(card)).thenReturn(card);

        Card result = useCase.execute(userId, cardId, "New title", null);

        assertEquals("New title", result.getTitle());
        assertEquals("Old desc", result.getDescription());

        verify(cardRepository).save(card);
    }

    @Test
    void should_not_save_if_nothing_changed() {
        Long userId = 1L;
        Long boardId = 100L;
        Long columnId = 10L;
        Long cardId = 1000L;

        Card card = new Card(cardId, columnId, "Title", "Desc", 0);
        Column column = new Column(columnId, boardId, "Todo", 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));

        Card result = useCase.execute(userId, cardId, "Title", "Desc");

        assertEquals("Title", result.getTitle());
        assertEquals("Desc", result.getDescription());

        verify(cardRepository, never()).save(any());
    }

    @Test
    void should_throw_when_card_not_found() {
        Long userId = 1L;
        Long cardId = 1000L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(userId, cardId, "New title", "desc")
        );

        assertEquals("Card not found", exception.getMessage());

        verify(cardRepository).findById(cardId);
        verifyNoInteractions(columnRepository, accessService);
    }

    @Test
    void should_throw_when_column_not_found() {
        Long userId = 1L;
        Long cardId = 1000L;
        Long columnId = 10L;

        Card card = new Card(cardId, columnId, "Title", "Desc", 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(columnRepository.findById(columnId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(userId, cardId, "New title", "desc")
        );

        assertEquals("Column not found", exception.getMessage());

        verify(columnRepository).findById(columnId);
        verify(accessService, never()).requireBoardOwner(any(), any());
    }
}