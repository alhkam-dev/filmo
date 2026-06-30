package com.alhkam.film_api.rest.dto;

import lombok.Builder;

@Builder
public record TokenResponseDTO(String accessToken, String tokenType, long expiresIn) {}
