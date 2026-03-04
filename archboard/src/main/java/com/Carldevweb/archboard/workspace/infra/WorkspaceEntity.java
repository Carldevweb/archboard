package com.Carldevweb.archboard.workspace.infra;

import com.Carldevweb.archboard.common.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "workspaces",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_workspaces_owner_name", columnNames = {"owner_id", "name"})
        }
)
public class WorkspaceEntity extends BaseEntity {

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    protected WorkspaceEntity() {
        // JPA
    }

    public WorkspaceEntity(Long ownerId, String name) {
        this.ownerId = ownerId;
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}