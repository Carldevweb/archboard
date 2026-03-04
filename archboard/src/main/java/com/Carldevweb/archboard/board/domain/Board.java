package com.Carldevweb.archboard.board.domain;

import java.util.Objects;

public class Board {

    private Long id;
    private Long workspaceId;
    private String name;

    public Board(Long id, Long workspaceId, String name) {
        if (workspaceId == null) throw new IllegalArgumentException("workspaceId is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");

        this.id = id;
        this.workspaceId = workspaceId;
        this.name = name.trim();
    }

    public static Board create(Long workspaceId, String name) {
        return new Board(null, workspaceId, name);
    }

    public Long getId() {
        return id;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public String getName() {
        return name;
    }

    void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}