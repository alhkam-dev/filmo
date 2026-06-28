package com.alhkam.film_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.jwt")
public record JwtConfigurationProperties(String secret, String duration) {}
