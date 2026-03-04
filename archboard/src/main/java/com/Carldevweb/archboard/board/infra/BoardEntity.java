package com.Carldevweb.archboard.board.infra;

import com.Carldevweb.archboard.common.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "boards",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_boards_workspace_name", columnNames = {"workspace_id", "name"})
        }
)
public class BoardEntity extends BaseEntity {

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    protected BoardEntity() {
        // JPA
    }

    public BoardEntity(Long workspaceId, String name) {
        this.workspaceId = workspaceId;
        this.name = name;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}