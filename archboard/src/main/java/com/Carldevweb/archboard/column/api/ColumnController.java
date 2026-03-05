package com.Carldevweb.archboard.column.api;

import com.Carldevweb.archboard.column.api.dto.MoveColumnRequest;
import com.Carldevweb.archboard.column.app.MoveColumnUseCase;
import com.Carldevweb.archboard.column.api.dto.ColumnResponse;
import com.Carldevweb.archboard.column.api.dto.CreateColumnRequest;
import com.Carldevweb.archboard.column.api.dto.UpdateColumnRequest;
import com.Carldevweb.archboard.column.app.CreateColumnUseCase;
import com.Carldevweb.archboard.column.app.DeleteColumnUseCase;
import com.Carldevweb.archboard.column.app.ListColumnsUseCase;
import com.Carldevweb.archboard.column.app.UpdateColumnUseCase;
import com.Carldevweb.archboard.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ColumnController {

    private final CurrentUser currentUser;
    private final CreateColumnUseCase createColumnUseCase;
    private final ListColumnsUseCase listColumnsUseCase;
    private final UpdateColumnUseCase updateColumnUseCase;
    private final DeleteColumnUseCase deleteColumnUseCase;

    private final MoveColumnUseCase moveColumnUseCase;

    public ColumnController(
            CurrentUser currentUser,
            CreateColumnUseCase createColumnUseCase,
            ListColumnsUseCase listColumnsUseCase,
            UpdateColumnUseCase updateColumnUseCase,
            DeleteColumnUseCase deleteColumnUseCase,
            MoveColumnUseCase moveColumnUseCase
    ) {
        this.currentUser = currentUser;
        this.createColumnUseCase = createColumnUseCase;
        this.listColumnsUseCase = listColumnsUseCase;
        this.updateColumnUseCase = updateColumnUseCase;
        this.deleteColumnUseCase = deleteColumnUseCase;
        this.moveColumnUseCase = moveColumnUseCase;
    }

    @PostMapping("/boards/{boardId}/columns")
    @ResponseStatus(HttpStatus.CREATED)
    public ColumnResponse create(@PathVariable Long boardId, @RequestBody @Valid CreateColumnRequest request) {
        Long userId = currentUser.id();
        var created = createColumnUseCase.execute(userId, boardId, request.name());
        return ColumnResponse.from(created);
    }

    @GetMapping("/boards/{boardId}/columns")
    public List<ColumnResponse> list(@PathVariable Long boardId) {
        Long userId = currentUser.id();
        return listColumnsUseCase.execute(userId, boardId)
                .stream()
                .map(ColumnResponse::from)
                .toList();
    }

    @PatchMapping("/columns/{columnId}")
    public ColumnResponse update(@PathVariable Long columnId, @RequestBody @Valid UpdateColumnRequest request) {
        Long userId = currentUser.id();
        var updated = updateColumnUseCase.execute(userId, columnId, request.name(), request.position());
        return ColumnResponse.from(updated);
    }

    @DeleteMapping("/columns/{columnId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long columnId) {
        Long userId = currentUser.id();
        deleteColumnUseCase.execute(userId, columnId);
    }

    @PatchMapping("/columns/{columnId}/move")
    public ColumnResponse move(@PathVariable Long columnId, @RequestBody @Valid MoveColumnRequest request) {
        Long userId = currentUser.id();
        var moved = moveColumnUseCase.execute(userId, columnId, request.position());
        return ColumnResponse.from(moved);
    }
}