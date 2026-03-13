package com.Carldevweb.archboard.activity.api.dto;

import com.Carldevweb.archboard.activity.domain.Activity;

import java.time.Instant;

public record ActivityResponse(
        Long id,
        Long boardId,
        String type,
        String entityType,
        Long entityId,
        String message,
        Instant createdAt
) {
    public static ActivityResponse from(Activity activity) {
        return new ActivityResponse(
                activity.getId(),
                activity.getBoardId(),
                activity.getType(),
                activity.getEntityType(),
                activity.getEntityId(),
                activity.getMessage(),
                activity.getCreatedAt()
        );
    }
}