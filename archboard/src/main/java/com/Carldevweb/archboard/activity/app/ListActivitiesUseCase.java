package com.Carldevweb.archboard.activity.app;

import com.Carldevweb.archboard.activity.domain.Activity;
import com.Carldevweb.archboard.activity.domain.ActivityRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListActivitiesUseCase {

    private final ActivityRepository activityRepository;
    private final AccessService accessService;

    public ListActivitiesUseCase(ActivityRepository activityRepository,
                                 AccessService accessService) {
        this.activityRepository = activityRepository;
        this.accessService = accessService;
    }

    public List<Activity> execute(Long userId, Long boardId) {
        accessService.requireBoardOwner(userId, boardId);
        return activityRepository.findByBoardId(boardId);
    }
}