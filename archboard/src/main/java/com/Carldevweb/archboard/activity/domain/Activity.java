package com.Carldevweb.archboard.activity.domain;

import java.time.Instant;

public class Activity {

    private Long id;
    private Long boardId;
    private String type;
    private String entityType;
    private Long entityId;
    private String message;
    private Instant createdAt;

    public Activity(Long id, Long boardId, String type, String entityType, Long entityId, String message, Instant createdAt) {
        this.id = id;
        this.boardId = boardId;
        this.type = type;
        this.entityType = entityType;
        this.entityId = entityId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getBoardId() { return boardId; }
    public String getType() { return type; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}