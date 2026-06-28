package com.alhkam.film_web.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record FilmPreviewDTO(
        Long id,
        String title,
        Integer releaseYear,
        UUID posterResourceId
) {}
