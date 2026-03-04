package com.Carldevweb.archboard.workspace.app;

import com.Carldevweb.archboard.workspace.domain.Workspace;
import com.Carldevweb.archboard.workspace.domain.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListMyWorkspacesUseCase {

    public record Item(Long id, String name) {}

    private final WorkspaceRepository repo;

    public ListMyWorkspacesUseCase(WorkspaceRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Item> execute(Long ownerId) {
        if (ownerId == null) throw new IllegalArgumentException("ownerId is required");

        List<Workspace> list = repo.findAllByOwnerId(ownerId);
        return list.stream()
                .map(w -> new Item(w.getId(), w.getName()))
                .toList();
    }
}