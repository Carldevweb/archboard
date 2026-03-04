package com.Carldevweb.archboard.board.domain;

import java.util.List;

public interface BoardRepository {

    Board save(Board board);

    List<Board> findAllByWorkspaceId(Long workspaceId);

    boolean existsByWorkspaceIdAndName(Long workspaceId, String name);
}