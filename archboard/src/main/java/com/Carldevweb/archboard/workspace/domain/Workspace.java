package com.Carldevweb.archboard.workspace.domain;

import java.util.Objects;

public class Workspace {

    private Long id;
    private Long ownerId;
    private String name;

    public Workspace(Long id, Long ownerId, String name) {
        if (ownerId == null) throw new IllegalArgumentException("ownerId is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");

        this.id = id;
        this.ownerId = ownerId;
        this.name = name.trim();
    }

    public static Workspace create(Long ownerId, String name) {
        return new Workspace(null, ownerId, name);
    }

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        this.name = newName.trim();
    }

    public Long getId() {
        return id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    void setId(Long id) { // utilisé par l'adapter infra
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workspace that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}