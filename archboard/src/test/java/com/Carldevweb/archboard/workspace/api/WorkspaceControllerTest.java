package com.Carldevweb.archboard.workspace.api;

import com.Carldevweb.archboard.security.CurrentUser;
import com.Carldevweb.archboard.workspace.api.dto.CreateWorkspaceRequest;
import com.Carldevweb.archboard.workspace.api.dto.RenameWorkspaceRequest;
import com.Carldevweb.archboard.workspace.app.CreateWorkspaceUseCase;
import com.Carldevweb.archboard.workspace.app.DeleteWorkspaceUseCase;
import com.Carldevweb.archboard.workspace.app.GetWorkspaceUseCase;
import com.Carldevweb.archboard.workspace.app.ListMyWorkspacesUseCase;
import com.Carldevweb.archboard.workspace.app.RenameWorkspaceUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.Carldevweb.archboard.common.api.ConflictException;
import com.Carldevweb.archboard.common.api.GlobalExceptionHandler;
import com.Carldevweb.archboard.common.api.NotFoundException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceControllerTest {

    @Mock
    private CurrentUser currentUser;
    @Mock
    private CreateWorkspaceUseCase create;
    @Mock
    private ListMyWorkspacesUseCase list;
    @Mock
    private GetWorkspaceUseCase get;
    @Mock
    private RenameWorkspaceUseCase rename;
    @Mock
    private DeleteWorkspaceUseCase delete;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        WorkspaceController controller = new WorkspaceController(
                currentUser, create, list, get, rename, delete
        );
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        when(currentUser.id()).thenReturn(1L);
    }

    @Test
    void should_return_404_when_workspace_not_found() throws Exception {
        when(get.execute(1L, 999L))
                .thenThrow(new NotFoundException("Workspace not found"));

        mockMvc.perform(get("/api/v1/workspaces/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Workspace not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/workspaces/999"));
    }

    @Test
    void should_create_workspace() throws Exception {
        CreateWorkspaceRequest request = new CreateWorkspaceRequest("Workspace A");
        when(create.execute(any())).thenReturn(new CreateWorkspaceUseCase.Result(10L, "Workspace A"));

        mockMvc.perform(post("/api/v1/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Workspace A"));
    }

    @Test
    void should_list_workspaces() throws Exception {
        when(list.execute(1L)).thenReturn(List.of(
                new ListMyWorkspacesUseCase.Item(10L, "Workspace A"),
                new ListMyWorkspacesUseCase.Item(11L, "Workspace B")
        ));

        mockMvc.perform(get("/api/v1/workspaces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Workspace A"))
                .andExpect(jsonPath("$[1].id").value(11))
                .andExpect(jsonPath("$[1].name").value("Workspace B"));
    }

    @Test
    void should_get_workspace_by_id() throws Exception {
        when(get.execute(1L, 10L)).thenReturn(new GetWorkspaceUseCase.Result(10L, "Workspace A"));

        mockMvc.perform(get("/api/v1/workspaces/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Workspace A"));
    }

    @Test
    void should_rename_workspace() throws Exception {
        RenameWorkspaceRequest request = new RenameWorkspaceRequest("Workspace Updated");
        when(rename.execute(any())).thenReturn(new RenameWorkspaceUseCase.Result(10L, "Workspace Updated"));

        mockMvc.perform(patch("/api/v1/workspaces/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Workspace Updated"));
    }

    @Test
    void should_delete_workspace() throws Exception {
        mockMvc.perform(delete("/api/v1/workspaces/10"))
                .andExpect(status().isNoContent());

        verify(delete).execute(1L, 10L);
    }
}