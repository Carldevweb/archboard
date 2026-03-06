package com.Carldevweb.archboard.activity.app;

import com.Carldevweb.archboard.activity.domain.Activity;
import com.Carldevweb.archboard.activity.domain.ActivityRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CreateActivityUseCase {

    private final ActivityRepository activityRepository;

    public CreateActivityUseCase(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public void log(Long boardId, String type, String entityType, Long entityId, String message) {

        Activity activity = new Activity(
                null,
                boardId,
                type,
                entityType,
                entityId,
                message,
                Instant.now()
        );

        activityRepository.save(activity);
    }
}