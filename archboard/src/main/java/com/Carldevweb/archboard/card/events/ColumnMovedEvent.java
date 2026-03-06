package com.Carldevweb.archboard.column.events;

import com.Carldevweb.archboard.common.events.DomainEvent;

public record ColumnMovedEvent(
        Long boardId,
        Long columnId,
        int position
) implements DomainEvent {}