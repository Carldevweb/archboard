package com.Carldevweb.archboard.activity.infra;

import com.Carldevweb.archboard.activity.domain.Activity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "activities")
public class ActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long boardId;

    private String type;

    private String entityType;

    private Long entityId;

    private String message;

    private Instant createdAt;

    public ActivityEntity() {}

    public ActivityEntity(Long id, Long boardId, String type, String entityType, Long entityId, String message, Instant createdAt) {
        this.id = id;
        this.boardId = boardId;
        this.type = type;
        this.entityType = entityType;
        this.entityId = entityId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public static ActivityEntity fromDomain(Activity a) {
        return new ActivityEntity(
                a.getId(),
                a.getBoardId(),
                a.getType(),
                a.getEntityType(),
                a.getEntityId(),
                a.getMessage(),
                a.getCreatedAt()
        );
    }

    public Activity toDomain() {
        return new Activity(id, boardId, type, entityType, entityId, message, createdAt);
    }
}