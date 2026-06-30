package com.alhkam.film_web.rating_api.dto;

import lombok.Builder;

@Builder
public record RatingDetailsDTO(int score, String created) {}
