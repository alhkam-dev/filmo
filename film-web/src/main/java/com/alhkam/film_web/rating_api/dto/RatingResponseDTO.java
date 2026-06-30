package com.alhkam.film_web.rating_api.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RatingResponseDTO(
    Long id, LocalDateTime created, Integer score, Long filmId, Long userId) {}
