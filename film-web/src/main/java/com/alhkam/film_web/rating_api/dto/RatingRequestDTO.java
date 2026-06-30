package com.alhkam.film_web.rating_api.dto;

import lombok.Builder;

@Builder
public record RatingRequestDTO(Long userId, Long filmId, Integer score) {}
