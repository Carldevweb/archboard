package com.Carldevweb.archboard.workspace.app;

import com.Carldevweb.archboard.workspace.domain.Workspace;
import com.Carldevweb.archboard.workspace.domain.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateWorkspaceUseCaseTest {

    @Mock
    private WorkspaceRepository repo;

    @InjectMocks
    private CreateWorkspaceUseCase useCase;

    @Test
    void should_create_workspace() {
        Long ownerId = 1L;

        CreateWorkspaceUseCase.Command command =
                new CreateWorkspaceUseCase.Command(ownerId, "  Mon workspace  ");

        Workspace savedWorkspace = Workspace.create(ownerId, "Mon workspace");

        when(repo.existsByOwnerIdAndName(ownerId, "Mon workspace")).thenReturn(false);
        when(repo.save(any(Workspace.class))).thenReturn(savedWorkspace);

        CreateWorkspaceUseCase.Result result = useCase.execute(command);

        assertNotNull(result);
        assertEquals(savedWorkspace.getId(), result.id());
        assertEquals("Mon workspace", result.name());

        verify(repo).existsByOwnerIdAndName(ownerId, "Mon workspace");

        ArgumentCaptor<Workspace> captor = ArgumentCaptor.forClass(Workspace.class);
        verify(repo).save(captor.capture());
        assertEquals(ownerId, captor.getValue().getOwnerId());
        assertEquals("Mon workspace", captor.getValue().getName());
    }

    @Test
    void should_throw_when_owner_id_is_null() {
        CreateWorkspaceUseCase.Command command =
                new CreateWorkspaceUseCase.Command(null, "Workspace");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(command)
        );

        assertEquals("ownerId is required", exception.getMessage());

        verifyNoInteractions(repo);
    }

    @Test
    void should_throw_when_name_is_blank() {
        CreateWorkspaceUseCase.Command command =
                new CreateWorkspaceUseCase.Command(1L, "   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(command)
        );

        assertEquals("name is required", exception.getMessage());

        verifyNoInteractions(repo);
    }

    @Test
    void should_throw_when_name_is_too_long() {
        String longName = "a".repeat(61);

        CreateWorkspaceUseCase.Command command =
                new CreateWorkspaceUseCase.Command(1L, longName);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(command)
        );

        assertEquals("name too long (max 60)", exception.getMessage());

        verifyNoInteractions(repo);
    }

    @Test
    void should_throw_when_workspace_name_already_exists_for_owner() {
        Long ownerId = 1L;

        CreateWorkspaceUseCase.Command command =
                new CreateWorkspaceUseCase.Command(ownerId, "Workspace");

        when(repo.existsByOwnerIdAndName(ownerId, "Workspace")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Workspace name already used", exception.getMessage());

        verify(repo).existsByOwnerIdAndName(ownerId, "Workspace");
        verify(repo, never()).save(any(Workspace.class));
    }
}