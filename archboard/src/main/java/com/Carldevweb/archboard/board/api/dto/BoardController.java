package com.Carldevweb.archboard.board.api.dto;

import com.Carldevweb.archboard.board.app.*;
import com.Carldevweb.archboard.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BoardController {

    private final CurrentUser currentUser;
    private final CreateBoardUseCase create;
    private final ListBoardsUseCase list;
    private final GetBoardUseCase get;
    private final RenameBoardUseCase rename;
    private final DeleteBoardUseCase delete;

    public BoardController(
            CurrentUser currentUser,
            CreateBoardUseCase create,
            ListBoardsUseCase list,
            GetBoardUseCase get,
            RenameBoardUseCase rename,
            DeleteBoardUseCase delete
    ) {
        this.currentUser = currentUser;
        this.create = create;
        this.list = list;
        this.get = get;
        this.rename = rename;
        this.delete = delete;
    }

    @PostMapping("/api/v1/workspaces/{workspaceId}/boards")
    @ResponseStatus(HttpStatus.CREATED)
    public BoardResponse create(
            @PathVariable("workspaceId") Long workspaceId,
            @RequestBody CreateBoardRequest req
    ) {
        var res = create.execute(new CreateBoardUseCase.Command(currentUser.id(), workspaceId, req.name()));
        return new BoardResponse(res.id(), res.name());
    }

    @GetMapping("/api/v1/workspaces/{workspaceId}/boards")
    public List<BoardResponse> list(@PathVariable("workspaceId") Long workspaceId) {
        return list.execute(currentUser.id(), workspaceId).stream()
                .map(it -> new BoardResponse(it.id(), it.name()))
                .toList();
    }

    @GetMapping("/api/v1/boards/{boardId}")
    public BoardDetailResponse getById(@PathVariable("boardId") Long boardId) {
        var res = get.execute(currentUser.id(), boardId);
        return new BoardDetailResponse(res.id(), res.workspaceId(), res.name());
    }

    @PatchMapping("/api/v1/boards/{boardId}")
    public BoardDetailResponse rename(@PathVariable("boardId") Long boardId, @RequestBody RenameBoardRequest req) {
        var res = rename.execute(new RenameBoardUseCase.Command(currentUser.id(), boardId, req.name()));
        return new BoardDetailResponse(res.id(), res.workspaceId(), res.name());
    }

    @DeleteMapping("/api/v1/boards/{boardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("boardId") Long boardId) {
        delete.execute(currentUser.id(), boardId);
    }
}