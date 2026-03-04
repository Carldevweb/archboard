package com.Carldevweb.archboard.workspace.domain;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository {

    Workspace save(Workspace workspace);

    Optional<Workspace> findByIdAndOwnerId(Long id, Long ownerId);

    List<Workspace> findAllByOwnerId(Long ownerId);

    boolean existsByOwnerIdAndName(Long ownerId, String name);

    void delete(Workspace workspace);
}