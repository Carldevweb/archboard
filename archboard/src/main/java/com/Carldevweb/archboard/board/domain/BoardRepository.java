package com.Carldevweb.archboard.board.domain;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    Board save(Board board);

    Optional<Board> findById(Long id);

    List<Board> findAllByWorkspaceId(Long workspaceId);

    boolean existsByWorkspaceIdAndName(Long workspaceId, String name);

    void delete(Board board);
}