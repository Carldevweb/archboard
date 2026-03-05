package com.Carldevweb.archboard.board.app;

import com.Carldevweb.archboard.board.domain.Board;
import com.Carldevweb.archboard.board.domain.BoardRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RenameBoardUseCase {

    public record Command(Long ownerId, Long boardId, String newName) {}
    public record Result(Long id, Long workspaceId, String name) {}

    private final AccessService access;
    private final BoardRepository boards;

    public RenameBoardUseCase(AccessService access, BoardRepository boards) {
        this.access = access;
        this.boards = boards;
    }

    @Transactional
    public Result execute(Command cmd) {
        if (cmd.ownerId() == null) throw new IllegalArgumentException("ownerId is required");
        if (cmd.boardId() == null) throw new IllegalArgumentException("boardId is required");

        String name = (cmd.newName() == null) ? null : cmd.newName().trim();
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (name.length() > 60) throw new IllegalArgumentException("name too long (max 60)");

        Board b = access.requireBoardOwner(cmd.ownerId(), cmd.boardId());

        if (b.getName().equalsIgnoreCase(name)) {
            return new Result(b.getId(), b.getWorkspaceId(), b.getName());
        }

        if (boards.existsByWorkspaceIdAndName(b.getWorkspaceId(), name)) {
            throw new ConflictException("Board name already used in this workspace");
        }

        Board saved = boards.save(new Board(b.getId(), b.getWorkspaceId(), name));
        return new Result(saved.getId(), saved.getWorkspaceId(), saved.getName());
    }
}