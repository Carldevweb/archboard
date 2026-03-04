package com.Carldevweb.archboard.workspace.api;

import com.Carldevweb.archboard.security.CurrentUser;
import com.Carldevweb.archboard.workspace.api.dto.CreateWorkspaceRequest;
import com.Carldevweb.archboard.workspace.api.dto.RenameWorkspaceRequest;
import com.Carldevweb.archboard.workspace.api.dto.WorkspaceResponse;
import com.Carldevweb.archboard.workspace.app.CreateWorkspaceUseCase;
import com.Carldevweb.archboard.workspace.app.DeleteWorkspaceUseCase;
import com.Carldevweb.archboard.workspace.app.GetWorkspaceUseCase;
import com.Carldevweb.archboard.workspace.app.ListMyWorkspacesUseCase;
import com.Carldevweb.archboard.workspace.app.RenameWorkspaceUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private final CurrentUser currentUser;

    private final CreateWorkspaceUseCase create;
    private final ListMyWorkspacesUseCase list;
    private final GetWorkspaceUseCase get;
    private final RenameWorkspaceUseCase rename;
    private final DeleteWorkspaceUseCase delete;

    public WorkspaceController(
            CurrentUser currentUser,
            CreateWorkspaceUseCase create,
            ListMyWorkspacesUseCase list,
            GetWorkspaceUseCase get,
            RenameWorkspaceUseCase rename,
            DeleteWorkspaceUseCase delete
    ) {
        this.currentUser = currentUser;
        this.create = create;
        this.list = list;
        this.get = get;
        this.rename = rename;
        this.delete = delete;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceResponse create(@RequestBody CreateWorkspaceRequest req) {
        var res = create.execute(new CreateWorkspaceUseCase.Command(currentUser.id(), req.name()));
        return new WorkspaceResponse(res.id(), res.name());
    }

    @GetMapping
    public List<WorkspaceResponse> listMine() {
        return list.execute(currentUser.id()).stream()
                .map(it -> new WorkspaceResponse(it.id(), it.name()))
                .toList();
    }

    @GetMapping("/{id}")
    public WorkspaceResponse getById(@PathVariable("id") Long id) {
        var res = get.execute(currentUser.id(), id);
        return new WorkspaceResponse(res.id(), res.name());
    }

    @PatchMapping("/{id}")
    public WorkspaceResponse rename(@PathVariable("id") Long id, @RequestBody RenameWorkspaceRequest req) {
        var res = rename.execute(new RenameWorkspaceUseCase.Command(currentUser.id(), id, req.name()));
        return new WorkspaceResponse(res.id(), res.name());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        delete.execute(currentUser.id(), id);
    }
}