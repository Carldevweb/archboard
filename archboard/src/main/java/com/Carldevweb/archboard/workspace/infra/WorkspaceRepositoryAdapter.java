package com.Carldevweb.archboard.workspace.infra;

import com.Carldevweb.archboard.workspace.domain.Workspace;
import com.Carldevweb.archboard.workspace.domain.WorkspaceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WorkspaceRepositoryAdapter implements WorkspaceRepository {

    private final SpringDataWorkspaceRepository jpa;

    public WorkspaceRepositoryAdapter(SpringDataWorkspaceRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Workspace save(Workspace workspace) {
        WorkspaceEntity entity;

        if (workspace.getId() == null) {
            entity = new WorkspaceEntity(workspace.getOwnerId(), workspace.getName());
        } else {
            entity = jpa.findById(workspace.getId()).orElseThrow();
            entity.setName(workspace.getName());
        }

        WorkspaceEntity saved = jpa.save(entity);

        Workspace out = new Workspace(saved.getId(), saved.getOwnerId(), saved.getName());
        return out;
    }

    @Override
    public Optional<Workspace> findByIdAndOwnerId(Long id, Long ownerId) {
        return jpa.findByIdAndOwnerId(id, ownerId)
                .map(e -> new Workspace(e.getId(), e.getOwnerId(), e.getName()));
    }

    @Override
    public List<Workspace> findAllByOwnerId(Long ownerId) {
        return jpa.findAllByOwnerIdOrderByUpdatedAtDesc(ownerId).stream()
                .map(e -> new Workspace(e.getId(), e.getOwnerId(), e.getName()))
                .toList();
    }

    @Override
    public boolean existsByOwnerIdAndName(Long ownerId, String name) {
        return jpa.existsByOwnerIdAndNameIgnoreCase(ownerId, name);
    }

    @Override
    public void delete(Workspace workspace) {
        if (workspace.getId() == null) return;
        jpa.deleteById(workspace.getId());
    }
}