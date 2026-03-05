package com.Carldevweb.archboard.column.infra;

import com.Carldevweb.archboard.column.domain.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "columns")
public class ColumnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // IMPORTANT : pas d'import jakarta.persistence.Column (conflit avec ton domaine Column)
    @jakarta.persistence.Column(name = "board_id", nullable = false)
    private Long boardId;

    @jakarta.persistence.Column(nullable = false, length = 120)
    private String name;

    @jakarta.persistence.Column(nullable = false)
    private int position;

    public ColumnEntity() {}

    public ColumnEntity(Long id, Long boardId, String name, int position) {
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

    public static ColumnEntity fromDomain(Column column) {
        return new ColumnEntity(
                column.getId(),
                column.getBoardId(),
                column.getName(),
                column.getPosition()
        );
    }

    public Column toDomain() {
        return new Column(id, boardId, name, position);
    }
}