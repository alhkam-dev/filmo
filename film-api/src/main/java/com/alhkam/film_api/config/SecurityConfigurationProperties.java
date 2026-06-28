package com.alhkam.film_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.security")
public record SecurityConfigurationProperties(String username, String password) {}
