package com.Carldevweb.archboard.workspace.app;

import com.Carldevweb.archboard.workspace.domain.Workspace;
import com.Carldevweb.archboard.workspace.domain.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateWorkspaceUseCase {

    public record Command(Long ownerId, String name) {}

    public record Result(Long id, String name) {}

    private final WorkspaceRepository repo;

    public CreateWorkspaceUseCase(WorkspaceRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Result execute(Command cmd) {
        String name = (cmd.name() == null) ? null : cmd.name().trim();

        if (cmd.ownerId() == null) throw new IllegalArgumentException("ownerId is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (name.length() > 60) throw new IllegalArgumentException("name too long (max 60)");

        if (repo.existsByOwnerIdAndName(cmd.ownerId(), name)) {
            throw new IllegalArgumentException("Workspace name already used");
        }

        Workspace created = Workspace.create(cmd.ownerId(), name);
        Workspace saved = repo.save(created);

        return new Result(saved.getId(), saved.getName());
    }
}