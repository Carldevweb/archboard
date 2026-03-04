package com.Carldevweb.archboard.workspace.app;

import com.Carldevweb.archboard.common.api.NotFoundException;
import com.Carldevweb.archboard.workspace.domain.Workspace;
import com.Carldevweb.archboard.workspace.domain.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetWorkspaceUseCase {

    public record Result(Long id, String name) {}

    private final WorkspaceRepository repo;

    public GetWorkspaceUseCase(WorkspaceRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Result execute(Long ownerId, Long workspaceId) {
        if (ownerId == null) throw new IllegalArgumentException("ownerId is required");
        if (workspaceId == null) throw new IllegalArgumentException("workspaceId is required");

        Workspace w = repo.findByIdAndOwnerId(workspaceId, ownerId)
                .orElseThrow(() -> new NotFoundException("Workspace not found"));

        return new Result(w.getId(), w.getName());
    }
}