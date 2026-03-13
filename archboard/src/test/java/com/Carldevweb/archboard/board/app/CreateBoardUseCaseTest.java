package com.Carldevweb.archboard.board.app;

import com.Carldevweb.archboard.board.domain.Board;
import com.Carldevweb.archboard.board.domain.BoardRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.ConflictException;
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
class CreateBoardUseCaseTest {

    @Mock
    private AccessService access;

    @Mock
    private BoardRepository boards;

    @InjectMocks
    private CreateBoardUseCase useCase;

    @Test
    void should_create_board() {
        Long ownerId = 1L;
        Long workspaceId = 100L;

        CreateBoardUseCase.Command command =
                new CreateBoardUseCase.Command(ownerId, workspaceId, "  Project Alpha  ");

        Board savedBoard = Board.create(workspaceId, "Project Alpha");

        when(boards.existsByWorkspaceIdAndName(workspaceId, "Project Alpha")).thenReturn(false);
        when(boards.save(any(Board.class))).thenReturn(savedBoard);

        CreateBoardUseCase.Result result = useCase.execute(command);

        assertNotNull(result);
        assertEquals(savedBoard.getId(), result.id());
        assertEquals("Project Alpha", result.name());

        verify(access).requireWorkspaceOwner(ownerId, workspaceId);
        verify(boards).existsByWorkspaceIdAndName(workspaceId, "Project Alpha");

        ArgumentCaptor<Board> captor = ArgumentCaptor.forClass(Board.class);
        verify(boards).save(captor.capture());
        assertEquals(workspaceId, captor.getValue().getWorkspaceId());
        assertEquals("Project Alpha", captor.getValue().getName());
    }

    @Test
    void should_throw_when_owner_id_is_null() {
        CreateBoardUseCase.Command command =
                new CreateBoardUseCase.Command(null, 100L, "Board");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(command)
        );

        assertEquals("ownerId is required", exception.getMessage());

        verifyNoInteractions(access, boards);
    }

    @Test
    void should_throw_when_workspace_id_is_null() {
        CreateBoardUseCase.Command command =
                new CreateBoardUseCase.Command(1L, null, "Board");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(command)
        );

        assertEquals("workspaceId is required", exception.getMessage());

        verifyNoInteractions(access, boards);
    }

    @Test
    void should_throw_when_name_is_blank() {
        CreateBoardUseCase.Command command =
                new CreateBoardUseCase.Command(1L, 100L, "   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(command)
        );

        assertEquals("name is required", exception.getMessage());

        verifyNoInteractions(access, boards);
    }

    @Test
    void should_throw_when_name_is_too_long() {
        String longName = "a".repeat(61);

        CreateBoardUseCase.Command command =
                new CreateBoardUseCase.Command(1L, 100L, longName);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(command)
        );

        assertEquals("name too long (max 60)", exception.getMessage());

        verifyNoInteractions(access, boards);
    }

    @Test
    void should_throw_when_board_name_already_exists_in_workspace() {
        Long ownerId = 1L;
        Long workspaceId = 100L;

        CreateBoardUseCase.Command command =
                new CreateBoardUseCase.Command(ownerId, workspaceId, "Board");

        when(boards.existsByWorkspaceIdAndName(workspaceId, "Board")).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> useCase.execute(command)
        );

        assertEquals("Board name already used in this workspace", exception.getMessage());

        verify(access).requireWorkspaceOwner(ownerId, workspaceId);
        verify(boards).existsByWorkspaceIdAndName(workspaceId, "Board");
        verify(boards, never()).save(any(Board.class));
    }
}