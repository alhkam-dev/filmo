package com.alhkam.film_api.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.jwt")
public record JwtConfigurationProperties(String secret, String duration) {}
