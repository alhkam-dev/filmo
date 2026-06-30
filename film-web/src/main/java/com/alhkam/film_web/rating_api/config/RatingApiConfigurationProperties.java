package com.alhkam.film_web.rating_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "film-api")
public record RatingApiConfigurationProperties(String baseUrl, String clientId, String clientSecret) {}
