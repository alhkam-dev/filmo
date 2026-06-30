package com.alhkam.film_web.rating_api.dto;

public record TokenResponseDTO(String accessToken, String tokenType, long expiresIn) {}
