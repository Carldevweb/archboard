package com.Carldevweb.archboard.board.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataBoardRepository extends JpaRepository<BoardEntity, Long> {

    List<BoardEntity> findAllByWorkspaceIdOrderByUpdatedAtDesc(Long workspaceId);

    boolean existsByWorkspaceIdAndNameIgnoreCase(Long workspaceId, String name);
}