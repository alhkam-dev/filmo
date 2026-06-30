package com.alhkam.film_web.service.impl;

import com.alhkam.film_web.domain.Rating;
import com.alhkam.film_web.rating_api.dto.RatingDetailsDTO;
import com.alhkam.film_web.rating_api.dto.RatingRequestDTO;
import com.alhkam.film_web.rating_api.facade.RatingApiFacade;
import com.alhkam.film_web.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

  private final RatingApiFacade ratingApiFacade;

  @Override
  @Transactional(readOnly = true)
  public Integer getUserRatingForFilm(Long userId, Long filmId) {

    try {
      RatingDetailsDTO ratingDetailsDTO = ratingApiFacade.getRatingAndCreationTime(filmId, userId);

      return ratingDetailsDTO != null ? ratingDetailsDTO.score() : null;
    } catch (HttpClientErrorException.NotFound e) {
      return null;
    } catch (Exception e) {
      log.error(
          "Error retrieving user rating from rating service REST for userId: {} and filmId: {}",
          userId,
          filmId);
      return null;
    }
  }

  @Override
  @Transactional
  public void saveRating(Long filmId, Integer score, Long userId) {
    RatingRequestDTO ratingRequestDTO =
        RatingRequestDTO.builder().filmId(filmId).userId(userId).score(score).build();

    try {
      ratingApiFacade.createRating(ratingRequestDTO);
    } catch (HttpClientErrorException.BadRequest e) {
      throw new IllegalStateException("User has already rated the film.", e);
    } catch (Exception e) {
      log.error("Error saving rating in service REST for filmId: {}", filmId, e);
      throw new RuntimeException(
          "Could not save rating due to a communication error with the external API.", e);
    }
  }
}
