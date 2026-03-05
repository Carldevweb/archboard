package com.Carldevweb.archboard.board.app;

import com.Carldevweb.archboard.board.domain.BoardRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListBoardsUseCase {

    public record Item(Long id, String name) {}

    private final AccessService access;
    private final BoardRepository boards;

    public ListBoardsUseCase(AccessService access, BoardRepository boards) {
        this.access = access;
        this.boards = boards;
    }

    @Transactional(readOnly = true)
    public List<Item> execute(Long ownerId, Long workspaceId) {
        if (ownerId == null) throw new IllegalArgumentException("ownerId is required");
        if (workspaceId == null) throw new IllegalArgumentException("workspaceId is required");

        // ✅ check accès centralisé
        access.requireWorkspaceOwner(ownerId, workspaceId);

        return boards.findAllByWorkspaceId(workspaceId).stream()
                .map(b -> new Item(b.getId(), b.getName()))
                .toList();
    }
}