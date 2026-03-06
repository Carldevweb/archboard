package com.Carldevweb.archboard.card.events;

import com.Carldevweb.archboard.common.events.DomainEvent;

public record CardMovedEvent(
        Long boardId,
        Long cardId,
        Long fromColumnId,
        Long toColumnId
) implements DomainEvent {}