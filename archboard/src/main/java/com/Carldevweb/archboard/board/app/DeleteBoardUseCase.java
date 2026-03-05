package com.Carldevweb.archboard.board.app;

import com.Carldevweb.archboard.board.domain.Board;
import com.Carldevweb.archboard.board.domain.BoardRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteBoardUseCase {

    private final AccessService access;
    private final BoardRepository boards;

    public DeleteBoardUseCase(AccessService access, BoardRepository boards) {
        this.access = access;
        this.boards = boards;
    }

    @Transactional
    public void execute(Long ownerId, Long boardId) {
        Board b = access.requireBoardOwner(ownerId, boardId);
        boards.delete(b);
    }
}