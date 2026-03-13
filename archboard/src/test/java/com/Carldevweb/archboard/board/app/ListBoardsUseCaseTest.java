package com.Carldevweb.archboard.board.app;

import com.Carldevweb.archboard.board.domain.Board;
import com.Carldevweb.archboard.board.domain.BoardRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListBoardsUseCaseTest {

    @Mock
    private AccessService access;

    @Mock
    private BoardRepository boards;

    @InjectMocks
    private ListBoardsUseCase useCase;

    @Test
    void should_list_boards_for_workspace() {
        Long ownerId = 1L;
        Long workspaceId = 100L;

        Board board1 = Board.create(workspaceId, "Board A");
        Board board2 = Board.create(workspaceId, "Board B");

        when(boards.findAllByWorkspaceId(workspaceId)).thenReturn(List.of(board1, board2));

        List<ListBoardsUseCase.Item> result = useCase.execute(ownerId, workspaceId);

        assertEquals(2, result.size());
        assertEquals(board1.getId(), result.get(0).id());
        assertEquals("Board A", result.get(0).name());
        assertEquals(board2.getId(), result.get(1).id());
        assertEquals("Board B", result.get(1).name());

        verify(access).requireWorkspaceOwner(ownerId, workspaceId);
        verify(boards).findAllByWorkspaceId(workspaceId);
    }

    @Test
    void should_return_empty_list_when_workspace_has_no_boards() {
        Long ownerId = 1L;
        Long workspaceId = 100L;

        when(boards.findAllByWorkspaceId(workspaceId)).thenReturn(List.of());

        List<ListBoardsUseCase.Item> result = useCase.execute(ownerId, workspaceId);

        assertTrue(result.isEmpty());

        verify(access).requireWorkspaceOwner(ownerId, workspaceId);
        verify(boards).findAllByWorkspaceId(workspaceId);
    }

    @Test
    void should_throw_when_owner_id_is_null() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(null, 100L)
        );

        assertEquals("ownerId is required", exception.getMessage());

        verifyNoInteractions(access, boards);
    }

    @Test
    void should_throw_when_workspace_id_is_null() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(1L, null)
        );

        assertEquals("workspaceId is required", exception.getMessage());

        verifyNoInteractions(access, boards);
    }
}