package com.Carldevweb.archboard.column.domain;

public class Column {

    private Long id;
    private Long boardId;
    private String name;
    private int position;

    public Column(Long id, Long boardId, String name, int position) {
        this.id = id;
        this.boardId = boardId;
        this.name = name;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public Long getBoardId() {
        return boardId;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void move(int position) {
        this.position = position;
    }
}