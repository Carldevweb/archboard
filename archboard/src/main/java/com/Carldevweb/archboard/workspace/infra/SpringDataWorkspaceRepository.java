package com.Carldevweb.archboard.workspace.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataWorkspaceRepository extends JpaRepository<WorkspaceEntity, Long> {

    Optional<WorkspaceEntity> findByIdAndOwnerId(Long id, Long ownerId);

    List<WorkspaceEntity> findAllByOwnerIdOrderByUpdatedAtDesc(Long ownerId);

    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);
}