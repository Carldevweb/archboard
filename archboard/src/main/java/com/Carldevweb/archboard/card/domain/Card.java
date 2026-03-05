package com.Carldevweb.archboard.card.domain;

public class Card {

    private Long id;
    private Long columnId;
    private String title;
    private String description;
    private int position;

    public Card(Long id, Long columnId, String title, String description, int position) {
        this.id = id;
        this.columnId = columnId;
        this.title = title;
        this.description = description;
        this.position = position;
    }

    public Long getId() { return id; }
    public Long getColumnId() { return columnId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPosition() { return position; }

    public void rename(String title) { this.title = title; }
    public void changeDescription(String description) { this.description = description; }

    public void moveTo(Long newColumnId, int newPosition) {
        this.columnId = newColumnId;
        this.position = newPosition;
    }

    public void moveInside(int newPosition) {
        this.position = newPosition;
    }
}