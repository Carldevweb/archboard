package com.Carldevweb.archboard.common.access;

import com.Carldevweb.archboard.board.domain.Board;
import com.Carldevweb.archboard.board.domain.BoardRepository;
import com.Carldevweb.archboard.common.api.NotFoundException;
import com.Carldevweb.archboard.workspace.domain.Workspace;
import com.Carldevweb.archboard.workspace.domain.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessServiceTest {

    @Mock
    private WorkspaceRepository workspaces;

    @Mock
    private BoardRepository boards;

    @InjectMocks
    private AccessService accessService;

    @Test
    void should_return_workspace_when_owner_matches() {
        Workspace workspace = mock(Workspace.class);

        when(workspaces.findByIdAndOwnerId(10L, 1L)).thenReturn(Optional.of(workspace));

        Workspace result = accessService.requireWorkspaceOwner(1L, 10L);

        assertEquals(workspace, result);
        verify(workspaces).findByIdAndOwnerId(10L, 1L);
    }

    @Test
    void should_throw_when_workspace_not_found_for_owner() {
        when(workspaces.findByIdAndOwnerId(10L, 1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> accessService.requireWorkspaceOwner(1L, 10L)
        );

        assertEquals("Workspace not found", exception.getMessage());
    }

    @Test
    void should_throw_when_workspace_owner_id_is_null() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accessService.requireWorkspaceOwner(null, 10L)
        );

        assertEquals("ownerId is required", exception.getMessage());
        verifyNoInteractions(workspaces, boards);
    }

    @Test
    void should_throw_when_workspace_id_is_null() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accessService.requireWorkspaceOwner(1L, null)
        );

        assertEquals("workspaceId is required", exception.getMessage());
        verifyNoInteractions(workspaces, boards);
    }

    @Test
    void should_return_board_when_owner_matches_workspace_owner() {
        Board board = mock(Board.class);
        Workspace workspace = mock(Workspace.class);

        when(board.getWorkspaceId()).thenReturn(100L);
        when(boards.findById(10L)).thenReturn(Optional.of(board));
        when(workspaces.findByIdAndOwnerId(100L, 1L)).thenReturn(Optional.of(workspace));

        Board result = accessService.requireBoardOwner(1L, 10L);

        assertEquals(board, result);
        verify(boards).findById(10L);
        verify(workspaces).findByIdAndOwnerId(100L, 1L);
    }

    @Test
    void should_throw_when_board_not_found() {
        when(boards.findById(10L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> accessService.requireBoardOwner(1L, 10L)
        );

        assertEquals("Board not found", exception.getMessage());
        verify(boards).findById(10L);
        verifyNoMoreInteractions(workspaces);
    }

    @Test
    void should_throw_when_board_workspace_not_owned_by_user() {
        Board board = mock(Board.class);

        when(board.getWorkspaceId()).thenReturn(100L);
        when(boards.findById(10L)).thenReturn(Optional.of(board));
        when(workspaces.findByIdAndOwnerId(100L, 1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> accessService.requireBoardOwner(1L, 10L)
        );

        assertEquals("Board not found", exception.getMessage());

        verify(boards).findById(10L);
        verify(workspaces).findByIdAndOwnerId(100L, 1L);
    }

    @Test
    void should_throw_when_board_owner_id_is_null() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accessService.requireBoardOwner(null, 10L)
        );

        assertEquals("ownerId is required", exception.getMessage());
        verifyNoInteractions(workspaces, boards);
    }

    @Test
    void should_throw_when_board_id_is_null() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accessService.requireBoardOwner(1L, null)
        );

        assertEquals("boardId is required", exception.getMessage());
        verifyNoInteractions(workspaces, boards);
    }
}