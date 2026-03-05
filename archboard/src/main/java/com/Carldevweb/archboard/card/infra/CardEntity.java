package com.Carldevweb.archboard.card.infra;

import com.Carldevweb.archboard.card.domain.Card;
import jakarta.persistence.*;

@Entity
@Table(
        name = "cards",
        indexes = {
                @Index(name = "idx_cards_column_id", columnList = "column_id"),
                @Index(name = "ux_cards_column_position", columnList = "column_id, position", unique = true)
        }
)
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(name = "column_id", nullable = false)
    private Long columnId;

    @jakarta.persistence.Column(nullable = false, length = 200)
    private String title;

    @jakarta.persistence.Column(columnDefinition = "text")
    private String description;

    @jakarta.persistence.Column(nullable = false)
    private int position;

    public CardEntity() {}

    public CardEntity(Long id, Long columnId, String title, String description, int position) {
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

    public static CardEntity fromDomain(Card card) {
        return new CardEntity(
                card.getId(),
                card.getColumnId(),
                card.getTitle(),
                card.getDescription(),
                card.getPosition()
        );
    }

    public Card toDomain() {
        return new Card(id, columnId, title, description, position);
    }
}