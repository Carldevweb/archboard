package com.Carldevweb.archboard.board.app;

import com.Carldevweb.archboard.board.domain.Board;
import com.Carldevweb.archboard.board.domain.BoardRepository;
import com.Carldevweb.archboard.common.api.ConflictException;
import com.Carldevweb.archboard.common.api.NotFoundException;
import com.Carldevweb.archboard.workspace.domain.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBoardUseCase {

    public record Command(Long ownerId, Long workspaceId, String name) {}
    public record Result(Long id, String name) {}

    private final WorkspaceRepository workspaces;
    private final BoardRepository boards;

    public CreateBoardUseCase(WorkspaceRepository workspaces, BoardRepository boards) {
        this.workspaces = workspaces;
        this.boards = boards;
    }

    @Transactional
    public Result execute(Command cmd) {
        if (cmd.ownerId() == null) throw new IllegalArgumentException("ownerId is required");
        if (cmd.workspaceId() == null) throw new IllegalArgumentException("workspaceId is required");

        String name = (cmd.name() == null) ? null : cmd.name().trim();
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (name.length() > 60) throw new IllegalArgumentException("name too long (max 60)");

        workspaces.findByIdAndOwnerId(cmd.workspaceId(), cmd.ownerId())
                .orElseThrow(() -> new NotFoundException("Workspace not found"));

        if (boards.existsByWorkspaceIdAndName(cmd.workspaceId(), name)) {
            throw new ConflictException("Board name already used in this workspace");
        }

        Board saved = boards.save(Board.create(cmd.workspaceId(), name));
        return new Result(saved.getId(), saved.getName());
    }
}