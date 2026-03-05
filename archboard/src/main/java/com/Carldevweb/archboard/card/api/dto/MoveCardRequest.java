package com.Carldevweb.archboard.card.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MoveCardRequest(
        @NotNull Long toColumnId,
        @NotNull @Min(0) Integer position
) {}