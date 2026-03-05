package com.Carldevweb.archboard.card.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCardRequest(
        @NotBlank @Size(max = 200) String title,
        String description
) {}