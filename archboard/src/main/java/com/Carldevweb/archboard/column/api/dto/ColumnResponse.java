package com.Carldevweb.archboard.column.api.dto;

import com.Carldevweb.archboard.column.domain.Column;

public record ColumnResponse(
        Long id,
        Long boardId,
        String name,
        int position
) {
    public static ColumnResponse from(Column c) {
        return new ColumnResponse(
                c.getId(),
                c.getBoardId(),
                c.getName(),
                c.getPosition()
        );
    }
}