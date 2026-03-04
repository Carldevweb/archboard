package com.Carldevweb.archboard.workspace.app;

import com.Carldevweb.archboard.common.api.ConflictException;
import com.Carldevweb.archboard.common.api.NotFoundException;
import com.Carldevweb.archboard.workspace.domain.Workspace;
import com.Carldevweb.archboard.workspace.domain.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RenameWorkspaceUseCase {

    public record Command(Long ownerId, Long workspaceId, String newName) {}
    public record Result(Long id, String name) {}

    private final WorkspaceRepository repo;

    public RenameWorkspaceUseCase(WorkspaceRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Result execute(Command cmd) {
        if (cmd.ownerId() == null) throw new IllegalArgumentException("ownerId is required");
        if (cmd.workspaceId() == null) throw new IllegalArgumentException("workspaceId is required");

        String name = (cmd.newName() == null) ? null : cmd.newName().trim();
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (name.length() > 60) throw new IllegalArgumentException("name too long (max 60)");

        Workspace w = repo.findByIdAndOwnerId(cmd.workspaceId(), cmd.ownerId())
                .orElseThrow(() -> new NotFoundException("Workspace not found"));

        // Optionnel mais utile : si c'est le même nom, on ne fait rien
        if (w.getName().equalsIgnoreCase(name)) {
            return new Result(w.getId(), w.getName());
        }

        if (repo.existsByOwnerIdAndName(cmd.ownerId(), name)) {
            throw new ConflictException("Workspace name already used");
        }

        w.rename(name);
        Workspace saved = repo.save(w);

        return new Result(saved.getId(), saved.getName());
    }
}