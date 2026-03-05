package com.Carldevweb.archboard.card.api.dto;

import jakarta.validation.constraints.Size;

public record UpdateCardRequest(
        @Size(max = 200) String title,
        String description
) {}