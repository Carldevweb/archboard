package com.Carldevweb.archboard.card.api;

import com.Carldevweb.archboard.card.api.dto.CreateCardRequest;
import com.Carldevweb.archboard.card.api.dto.MoveCardRequest;
import com.Carldevweb.archboard.card.api.dto.UpdateCardRequest;
import com.Carldevweb.archboard.card.app.CreateCardUseCase;
import com.Carldevweb.archboard.card.app.DeleteCardUseCase;
import com.Carldevweb.archboard.card.app.ListCardsUseCase;
import com.Carldevweb.archboard.card.app.MoveCardUseCase;
import com.Carldevweb.archboard.card.app.UpdateCardUseCase;
import com.Carldevweb.archboard.card.domain.Card;
import com.Carldevweb.archboard.common.api.GlobalExceptionHandler;
import com.Carldevweb.archboard.common.api.NotFoundException;
import com.Carldevweb.archboard.security.CurrentUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CurrentUser currentUser;
    @Mock
    private CreateCardUseCase createCardUseCase;
    @Mock
    private ListCardsUseCase listCardsUseCase;
    @Mock
    private UpdateCardUseCase updateCardUseCase;
    @Mock
    private MoveCardUseCase moveCardUseCase;
    @Mock
    private DeleteCardUseCase deleteCardUseCase;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        CardController controller = new CardController(
                currentUser,
                createCardUseCase,
                listCardsUseCase,
                updateCardUseCase,
                moveCardUseCase,
                deleteCardUseCase
        );
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        when(currentUser.id()).thenReturn(1L);
    }

    @Test
    void should_return_404_when_card_not_found_on_move() throws Exception {
        MoveCardRequest request = new MoveCardRequest(200L, 1);

        when(moveCardUseCase.execute(1L, 999L, 200L, 1))
                .thenThrow(new NotFoundException("Card not found"));

        mockMvc.perform(patch("/api/v1/cards/999/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Card not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/cards/999/move"));
    }

    @Test
    void should_return_404_when_card_not_found_on_update() throws Exception {
        UpdateCardRequest request = new UpdateCardRequest("Updated", "desc");

        when(updateCardUseCase.execute(1L, 999L, "Updated", "desc"))
                .thenThrow(new NotFoundException("Card not found"));

        mockMvc.perform(patch("/api/v1/cards/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Card not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/cards/999"));
    }

    @Test
    void should_create_card() throws Exception {
        CreateCardRequest request = new CreateCardRequest("Task", "desc");
        Card created = new Card(10L, 100L, "Task", "desc", 0);

        when(createCardUseCase.execute(1L, 100L, "Task", "desc")).thenReturn(created);

        mockMvc.perform(post("/api/v1/columns/100/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.columnId").value(100))
                .andExpect(jsonPath("$.title").value("Task"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.position").value(0));
    }

    @Test
    void should_list_cards() throws Exception {
        when(listCardsUseCase.execute(1L, 100L)).thenReturn(List.of(
                new Card(10L, 100L, "Task A", "desc", 0),
                new Card(11L, 100L, "Task B", "desc", 1)
        ));

        mockMvc.perform(get("/api/v1/columns/100/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].title").value("Task A"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].title").value("Task B"));
    }

    @Test
    void should_update_card() throws Exception {
        UpdateCardRequest request = new UpdateCardRequest("Updated", "new desc");
        Card updated = new Card(10L, 100L, "Updated", "new desc", 0);

        when(updateCardUseCase.execute(1L, 10L, "Updated", "new desc")).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/cards/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.description").value("new desc"));
    }

    @Test
    void should_move_card() throws Exception {
        MoveCardRequest request = new MoveCardRequest(200L, 1);
        Card moved = new Card(10L, 200L, "Task", "desc", 1);

        when(moveCardUseCase.execute(1L, 10L, 200L, 1)).thenReturn(moved);

        mockMvc.perform(patch("/api/v1/cards/10/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.columnId").value(200))
                .andExpect(jsonPath("$.position").value(1));
    }

    @Test
    void should_delete_card() throws Exception {
        mockMvc.perform(delete("/api/v1/cards/10"))
                .andExpect(status().isNoContent());

        verify(deleteCardUseCase).execute(1L, 10L);
    }
}