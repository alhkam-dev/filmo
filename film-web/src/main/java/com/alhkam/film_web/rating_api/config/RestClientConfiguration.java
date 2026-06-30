package com.alhkam.film_web.rating_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(RatingApiConfigurationProperties.class)
@RequiredArgsConstructor
public class RestClientConfiguration {

  private final RatingApiConfigurationProperties ratingApiConfigurationProperties;

  @Bean
  public RestClient ratingsApiClient() {
    return RestClient.builder()
        .baseUrl(ratingApiConfigurationProperties.baseUrl())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
