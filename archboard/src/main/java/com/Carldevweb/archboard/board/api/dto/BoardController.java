package com.Carldevweb.archboard.board.api;

import com.Carldevweb.archboard.board.api.dto.BoardResponse;
import com.Carldevweb.archboard.board.api.dto.CreateBoardRequest;
import com.Carldevweb.archboard.board.app.CreateBoardUseCase;
import com.Carldevweb.archboard.board.app.ListBoardsUseCase;
import com.Carldevweb.archboard.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/boards")
public class BoardController {

    private final CurrentUser currentUser;
    private final CreateBoardUseCase create;
    private final ListBoardsUseCase list;

    public BoardController(CurrentUser currentUser, CreateBoardUseCase create, ListBoardsUseCase list) {
        this.currentUser = currentUser;
        this.create = create;
        this.list = list;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoardResponse create(
            @PathVariable("workspaceId") Long workspaceId,
            @RequestBody CreateBoardRequest req
    ) {
        var res = create.execute(new CreateBoardUseCase.Command(currentUser.id(), workspaceId, req.name()));
        return new BoardResponse(res.id(), res.name());
    }

    @GetMapping
    public List<BoardResponse> list(@PathVariable("workspaceId") Long workspaceId) {
        return list.execute(currentUser.id(), workspaceId).stream()
                .map(it -> new BoardResponse(it.id(), it.name()))
                .toList();
    }
}