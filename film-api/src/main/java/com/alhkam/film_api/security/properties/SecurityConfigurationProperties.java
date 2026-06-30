package com.alhkam.film_api.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application.security")
public record SecurityConfigurationProperties(List<UserConfig> users) {
  public record UserConfig(String username, String password, List<String> authorities) {}
}
