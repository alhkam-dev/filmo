package com.alhkam.film_web.rating_api.dto;

import lombok.*;

@Builder
public record RatingAverageResponseDTO(double average, long ratings) {}
