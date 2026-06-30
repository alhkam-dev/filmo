package com.alhkam.film_web.rating_api.facade.impl;

import com.alhkam.film_web.rating_api.dto.RatingAverageResponseDTO;
import com.alhkam.film_web.rating_api.dto.RatingDetailsDTO;
import com.alhkam.film_web.rating_api.dto.RatingRequestDTO;
import com.alhkam.film_web.rating_api.dto.RatingResponseDTO;
import com.alhkam.film_web.rating_api.facade.RatingApiFacade;
import com.alhkam.film_web.rating_api.service.RatingAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class RatingApiFacadeImpl implements RatingApiFacade {

  private final RestClient ratingApiClient;
  private final RatingAuthService ratingAuthService;

  public RatingAverageResponseDTO getFilmRatingAverage(Long filmId) {
    return ratingApiClient
        .get()
        .uri("/ratings-average/films/{filmId}", filmId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ratingAuthService.getAccessToken())
        .retrieve()
        .body(RatingAverageResponseDTO.class);
  }

  public RatingResponseDTO createRating(RatingRequestDTO ratingRequestDTO) {
    return ratingApiClient
        .post()
        .uri("/ratings")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ratingAuthService.getAccessToken())
        .body(ratingRequestDTO)
        .retrieve()
        .body(RatingResponseDTO.class);
  }

  public RatingDetailsDTO getRatingAndCreationTime(Long filmId, Long userId) {
    return ratingApiClient
        .get()
        .uri("/ratings/films/{filmId}/users/{userId}", filmId, userId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ratingAuthService.getAccessToken())
        .retrieve()
        .body(RatingDetailsDTO.class);
  }
}
