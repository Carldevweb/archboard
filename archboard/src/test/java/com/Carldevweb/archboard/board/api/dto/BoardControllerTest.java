package com.Carldevweb.archboard.board.api.dto;

import com.Carldevweb.archboard.board.app.CreateBoardUseCase;
import com.Carldevweb.archboard.board.app.DeleteBoardUseCase;
import com.Carldevweb.archboard.board.app.GetBoardUseCase;
import com.Carldevweb.archboard.board.app.ListBoardsUseCase;
import com.Carldevweb.archboard.board.app.RenameBoardUseCase;
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
class BoardControllerTest {

    @Mock
    private CurrentUser currentUser;
    @Mock
    private CreateBoardUseCase create;
    @Mock
    private ListBoardsUseCase list;
    @Mock
    private GetBoardUseCase get;
    @Mock
    private RenameBoardUseCase rename;
    @Mock
    private DeleteBoardUseCase delete;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        BoardController controller = new BoardController(currentUser, create, list, get, rename, delete);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        when(currentUser.id()).thenReturn(1L);
    }

    @Test
    void should_return_404_when_board_not_found() throws Exception {
        when(get.execute(1L, 999L))
                .thenThrow(new NotFoundException("Board not found"));

        mockMvc.perform(get("/api/v1/boards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Board not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/boards/999"));
    }

    @Test
    void should_return_400_when_board_name_is_invalid() throws Exception {
        CreateBoardRequest request = new CreateBoardRequest("");

        when(create.execute(any()))
                .thenThrow(new IllegalArgumentException("name is required"));

        mockMvc.perform(post("/api/v1/workspaces/100/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("name is required"))
                .andExpect(jsonPath("$.path").value("/api/v1/workspaces/100/boards"));
    }

    @Test
    void should_create_board() throws Exception {
        CreateBoardRequest request = new CreateBoardRequest("Board A");
        when(create.execute(any())).thenReturn(new CreateBoardUseCase.Result(10L, "Board A"));

        mockMvc.perform(post("/api/v1/workspaces/100/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Board A"));
    }

    @Test
    void should_list_boards() throws Exception {
        when(list.execute(1L, 100L)).thenReturn(List.of(
                new ListBoardsUseCase.Item(10L, "Board A"),
                new ListBoardsUseCase.Item(11L, "Board B")
        ));

        mockMvc.perform(get("/api/v1/workspaces/100/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Board A"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].name").value("Board B"));
    }

    @Test
    void should_get_board_by_id() throws Exception {
        when(get.execute(1L, 10L)).thenReturn(new GetBoardUseCase.Result(10L, 100L, "Board A"));

        mockMvc.perform(get("/api/v1/boards/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.workspaceId").value(100))
                .andExpect(jsonPath("$.name").value("Board A"));
    }

    @Test
    void should_rename_board() throws Exception {
        RenameBoardRequest request = new RenameBoardRequest("Board Updated");
        when(rename.execute(any())).thenReturn(new RenameBoardUseCase.Result(10L, 100L, "Board Updated"));

        mockMvc.perform(patch("/api/v1/boards/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.workspaceId").value(100))
                .andExpect(jsonPath("$.name").value("Board Updated"));
    }

    @Test
    void should_delete_board() throws Exception {
        mockMvc.perform(delete("/api/v1/boards/10"))
                .andExpect(status().isNoContent());

        verify(delete).execute(1L, 10L);
    }
}