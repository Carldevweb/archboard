package com.Carldevweb.archboard.card.api.dto;

import com.Carldevweb.archboard.card.domain.Card;

public record CardResponse(
        Long id,
        Long columnId,
        String title,
        String description,
        int position
) {
    public static CardResponse from(Card c) {
        return new CardResponse(c.getId(), c.getColumnId(), c.getTitle(), c.getDescription(), c.getPosition());
    }
}