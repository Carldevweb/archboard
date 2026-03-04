package com.Carldevweb.archboard.board.app;

import com.Carldevweb.archboard.board.domain.BoardRepository;
import com.Carldevweb.archboard.common.api.NotFoundException;
import com.Carldevweb.archboard.workspace.domain.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListBoardsUseCase {

    public record Item(Long id, String name) {}

    private final WorkspaceRepository workspaces;
    private final BoardRepository boards;

    public ListBoardsUseCase(WorkspaceRepository workspaces, BoardRepository boards) {
        this.workspaces = workspaces;
        this.boards = boards;
    }

    @Transactional(readOnly = true)
    public List<Item> execute(Long ownerId, Long workspaceId) {
        if (ownerId == null) throw new IllegalArgumentException("ownerId is required");
        if (workspaceId == null) throw new IllegalArgumentException("workspaceId is required");

        workspaces.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new NotFoundException("Workspace not found"));

        return boards.findAllByWorkspaceId(workspaceId).stream()
                .map(b -> new Item(b.getId(), b.getName()))
                .toList();
    }
}