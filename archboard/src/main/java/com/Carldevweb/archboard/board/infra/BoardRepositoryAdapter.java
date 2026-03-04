package com.Carldevweb.archboard.board.infra;

import com.Carldevweb.archboard.board.domain.Board;
import com.Carldevweb.archboard.board.domain.BoardRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardRepositoryAdapter implements BoardRepository {

    private final SpringDataBoardRepository jpa;

    public BoardRepositoryAdapter(SpringDataBoardRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Board save(Board board) {
        BoardEntity entity = new BoardEntity(board.getWorkspaceId(), board.getName());
        BoardEntity saved = jpa.save(entity);
        return new Board(saved.getId(), saved.getWorkspaceId(), saved.getName());
    }

    @Override
    public List<Board> findAllByWorkspaceId(Long workspaceId) {
        return jpa.findAllByWorkspaceIdOrderByUpdatedAtDesc(workspaceId).stream()
                .map(e -> new Board(e.getId(), e.getWorkspaceId(), e.getName()))
                .toList();
    }

    @Override
    public boolean existsByWorkspaceIdAndName(Long workspaceId, String name) {
        return jpa.existsByWorkspaceIdAndNameIgnoreCase(workspaceId, name);
    }
}