package com.Carldevweb.archboard.column.api.dto;

import jakarta.validation.constraints.Size;

public record UpdateColumnRequest(
        @Size(max = 120)
        String name,
        Integer position
) {}