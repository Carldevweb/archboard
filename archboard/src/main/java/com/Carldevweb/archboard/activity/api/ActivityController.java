package com.Carldevweb.archboard.activity.api;

import com.Carldevweb.archboard.activity.api.dto.ActivityResponse;
import com.Carldevweb.archboard.activity.app.ListActivitiesUseCase;
import com.Carldevweb.archboard.security.CurrentUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivityController {

    private final CurrentUser currentUser;
    private final ListActivitiesUseCase listActivitiesUseCase;

    public ActivityController(CurrentUser currentUser,
                              ListActivitiesUseCase listActivitiesUseCase) {
        this.currentUser = currentUser;
        this.listActivitiesUseCase = listActivitiesUseCase;
    }

    @GetMapping("/api/v1/boards/{boardId}/activities")
    public List<ActivityResponse> listByBoard(@PathVariable Long boardId) {
        Long userId = currentUser.id();

        return listActivitiesUseCase.execute(userId, boardId)
                .stream()
                .map(ActivityResponse::from)
                .toList();
    }
}