package com.Carldevweb.archboard.column.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateColumnRequest(
        @NotBlank
        @Size(max = 120)
        String name
) {}