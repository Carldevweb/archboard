package com.Carldevweb.archboard.board.app;

import com.Carldevweb.archboard.board.domain.Board;
import com.Carldevweb.archboard.common.access.AccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetBoardUseCase {

    public record Result(Long id, Long workspaceId, String name) {}

    private final AccessService access;

    public GetBoardUseCase(AccessService access) {
        this.access = access;
    }

    @Transactional(readOnly = true)
    public Result execute(Long ownerId, Long boardId) {
        Board b = access.requireBoardOwner(ownerId, boardId);
        return new Result(b.getId(), b.getWorkspaceId(), b.getName());
    }
}