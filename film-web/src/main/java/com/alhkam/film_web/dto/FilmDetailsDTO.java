package com.alhkam.film_web.dto;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record FilmDetailsDTO(
        Long id,
        String title,
        Integer releaseYear,
        String directorFullName,
        Set<String> actorsFullNames,
        UUID posterResourceId,
        String averageRating
) {}
