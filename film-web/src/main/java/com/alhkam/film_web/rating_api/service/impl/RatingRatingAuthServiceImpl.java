package com.alhkam.film_web.rating_api.service.impl;

import com.alhkam.film_web.rating_api.config.RatingApiConfigurationProperties;
import com.alhkam.film_web.rating_api.dto.TokenResponseDTO;
import com.alhkam.film_web.rating_api.service.RatingAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingRatingAuthServiceImpl implements RatingAuthService {

  private final RestClient ratingApiClient;
  private final RatingApiConfigurationProperties ratingApiConfigurationProperties;

  public String getAccessToken() {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.put("grant_type", List.of("client_credentials"));

    TokenResponseDTO tokenResponse =
        ratingApiClient
            .post()
            .uri("/authenticate")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .headers(
                headers ->
                    headers.setBasicAuth(
                        ratingApiConfigurationProperties.clientId(),
                        ratingApiConfigurationProperties.clientSecret()))
            .body(body)
            .retrieve()
            .body(TokenResponseDTO.class);

    if (tokenResponse == null || tokenResponse.accessToken() == null) {
      throw new RuntimeException(
          "Error: No se pudo obtener el token de acceso del servicio REST rating-api");
    }

    return tokenResponse.accessToken();
  }
}
