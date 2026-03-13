package com.Carldevweb.archboard.common.access;

import com.Carldevweb.archboard.board.domain.Board;
import com.Carldevweb.archboard.board.domain.BoardRepository;
import com.Carldevweb.archboard.common.api.NotFoundException;
import com.Carldevweb.archboard.workspace.domain.Workspace;
import com.Carldevweb.archboard.workspace.domain.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccessService {

    private final WorkspaceRepository workspaces;
    private final BoardRepository boards;

    public AccessService(WorkspaceRepository workspaces, BoardRepository boards) {
        this.workspaces = workspaces;
        this.boards = boards;
    }

    @Transactional(readOnly = true)
    public Workspace requireWorkspaceOwner(Long ownerId, Long workspaceId) {
        if (ownerId == null) throw new IllegalArgumentException("ownerId is required");
        if (workspaceId == null) throw new IllegalArgumentException("workspaceId is required");

        return workspaces.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new NotFoundException("Workspace not found"));
    }

    @Transactional(readOnly = true)
    public Board requireBoardOwner(Long ownerId, Long boardId) {
        if (ownerId == null) throw new IllegalArgumentException("ownerId is required");
        if (boardId == null) throw new IllegalArgumentException("boardId is required");

        Board b = boards.findById(boardId)
                .orElseThrow(() -> new NotFoundException("Board not found"));

        System.out.println("ACCESS CHECK - ownerId = " + ownerId);
        System.out.println("ACCESS CHECK - boardId = " + boardId);
        System.out.println("ACCESS CHECK - workspaceId = " + b.getWorkspaceId());

        // sécurité : le board doit appartenir à un workspace du owner
        workspaces.findByIdAndOwnerId(b.getWorkspaceId(), ownerId)
                .orElseThrow(() -> new NotFoundException("Board not found"));

        return b;
    }

    // Prévu pour la suite :
    // public Column requireColumnOwner(Long ownerId, Long columnId) { ... }
}