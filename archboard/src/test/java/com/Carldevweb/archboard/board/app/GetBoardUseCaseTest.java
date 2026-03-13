package com.Carldevweb.archboard.board.app;

import com.Carldevweb.archboard.board.domain.Board;
import com.Carldevweb.archboard.common.access.AccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBoardUseCaseTest {

    @Mock
    private AccessService access;

    @InjectMocks
    private GetBoardUseCase useCase;

    @Test
    void should_return_board_details() {
        Long ownerId = 1L;
        Long boardId = 10L;

        Board board = Board.create(100L, "Project Alpha");

        when(access.requireBoardOwner(ownerId, boardId)).thenReturn(board);

        GetBoardUseCase.Result result = useCase.execute(ownerId, boardId);

        assertEquals(board.getId(), result.id());
        assertEquals(board.getWorkspaceId(), result.workspaceId());
        assertEquals(board.getName(), result.name());

        verify(access).requireBoardOwner(ownerId, boardId);
    }
}