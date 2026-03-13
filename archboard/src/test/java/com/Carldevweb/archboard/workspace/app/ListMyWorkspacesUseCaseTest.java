package com.Carldevweb.archboard.workspace.app;

import com.Carldevweb.archboard.workspace.domain.Workspace;
import com.Carldevweb.archboard.workspace.domain.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListMyWorkspacesUseCaseTest {

    @Mock
    private WorkspaceRepository repo;

    @InjectMocks
    private ListMyWorkspacesUseCase useCase;

    @Test
    void should_list_workspaces_for_owner() {
        Long ownerId = 1L;

        Workspace workspace1 = Workspace.create(ownerId, "Workspace A");
        Workspace workspace2 = Workspace.create(ownerId, "Workspace B");

        when(repo.findAllByOwnerId(ownerId)).thenReturn(List.of(workspace1, workspace2));

        List<ListMyWorkspacesUseCase.Item> result = useCase.execute(ownerId);

        assertEquals(2, result.size());
        assertEquals(workspace1.getId(), result.get(0).id());
        assertEquals("Workspace A", result.get(0).name());
        assertEquals(workspace2.getId(), result.get(1).id());
        assertEquals("Workspace B", result.get(1).name());

        verify(repo).findAllByOwnerId(ownerId);
    }

    @Test
    void should_return_empty_list_when_owner_has_no_workspaces() {
        Long ownerId = 1L;

        when(repo.findAllByOwnerId(ownerId)).thenReturn(List.of());

        List<ListMyWorkspacesUseCase.Item> result = useCase.execute(ownerId);

        assertTrue(result.isEmpty());
        verify(repo).findAllByOwnerId(ownerId);
    }

    @Test
    void should_throw_when_owner_id_is_null() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(null)
        );

        assertEquals("ownerId is required", exception.getMessage());

        verifyNoInteractions(repo);
    }
}