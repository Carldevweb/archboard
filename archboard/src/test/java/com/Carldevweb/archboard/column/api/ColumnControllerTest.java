package com.Carldevweb.archboard.column.api;

import com.Carldevweb.archboard.column.api.dto.CreateColumnRequest;
import com.Carldevweb.archboard.column.api.dto.MoveColumnRequest;
import com.Carldevweb.archboard.column.api.dto.UpdateColumnRequest;
import com.Carldevweb.archboard.column.app.CreateColumnUseCase;
import com.Carldevweb.archboard.column.app.DeleteColumnUseCase;
import com.Carldevweb.archboard.column.app.ListColumnsUseCase;
import com.Carldevweb.archboard.column.app.MoveColumnUseCase;
import com.Carldevweb.archboard.column.app.UpdateColumnUseCase;
import com.Carldevweb.archboard.column.domain.Column;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ColumnControllerTest {

    @Mock
    private CurrentUser currentUser;
    @Mock
    private CreateColumnUseCase createColumnUseCase;
    @Mock
    private ListColumnsUseCase listColumnsUseCase;
    @Mock
    private UpdateColumnUseCase updateColumnUseCase;
    @Mock
    private DeleteColumnUseCase deleteColumnUseCase;
    @Mock
    private MoveColumnUseCase moveColumnUseCase;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ColumnController controller = new ColumnController(
                currentUser,
                createColumnUseCase,
                listColumnsUseCase,
                updateColumnUseCase,
                deleteColumnUseCase,
                moveColumnUseCase
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void should_return_400_when_create_column_payload_is_invalid() throws Exception {
        CreateColumnRequest request = new CreateColumnRequest("");

        mockMvc.perform(post("/api/v1/boards/100/columns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createColumnUseCase);
    }

    @Test
    void should_return_404_when_column_not_found_on_move() throws Exception {
        when(currentUser.id()).thenReturn(1L);

        MoveColumnRequest request = new MoveColumnRequest(2);

        when(moveColumnUseCase.execute(1L, 999L, 2))
                .thenThrow(new NotFoundException("Column not found"));

        mockMvc.perform(patch("/api/v1/columns/999/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Column not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/columns/999/move"));
    }

    @Test
    void should_return_404_when_column_not_found_on_update() throws Exception {
        when(currentUser.id()).thenReturn(1L);

        UpdateColumnRequest request = new UpdateColumnRequest("Updated", null);

        when(updateColumnUseCase.execute(1L, 999L, "Updated", null))
                .thenThrow(new NotFoundException("Column not found"));

        mockMvc.perform(patch("/api/v1/columns/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Column not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/columns/999"));
    }

    @Test
    void should_create_column() throws Exception {
        when(currentUser.id()).thenReturn(1L);

        CreateColumnRequest request = new CreateColumnRequest("Todo");
        Column created = new Column(10L, 100L, "Todo", 0);

        when(createColumnUseCase.execute(1L, 100L, "Todo")).thenReturn(created);

        mockMvc.perform(post("/api/v1/boards/100/columns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.boardId").value(100))
                .andExpect(jsonPath("$.name").value("Todo"))
                .andExpect(jsonPath("$.position").value(0));
    }

    @Test
    void should_list_columns() throws Exception {
        when(currentUser.id()).thenReturn(1L);

        when(listColumnsUseCase.execute(1L, 100L)).thenReturn(List.of(
                new Column(10L, 100L, "Todo", 0),
                new Column(11L, 100L, "Done", 1)
        ));

        mockMvc.perform(get("/api/v1/boards/100/columns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Todo"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].name").value("Done"));
    }

    @Test
    void should_update_column() throws Exception {
        when(currentUser.id()).thenReturn(1L);

        UpdateColumnRequest request = new UpdateColumnRequest("Updated", null);
        Column updated = new Column(10L, 100L, "Updated", 0);

        when(updateColumnUseCase.execute(1L, 10L, "Updated", null)).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/columns/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void should_move_column() throws Exception {
        when(currentUser.id()).thenReturn(1L);

        MoveColumnRequest request = new MoveColumnRequest(2);
        Column moved = new Column(10L, 100L, "Todo", 2);

        when(moveColumnUseCase.execute(1L, 10L, 2)).thenReturn(moved);

        mockMvc.perform(patch("/api/v1/columns/10/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.position").value(2));
    }

    @Test
    void should_delete_column() throws Exception {
        when(currentUser.id()).thenReturn(1L);

        mockMvc.perform(delete("/api/v1/columns/10"))
                .andExpect(status().isNoContent());

        verify(deleteColumnUseCase).execute(1L, 10L);
    }
}