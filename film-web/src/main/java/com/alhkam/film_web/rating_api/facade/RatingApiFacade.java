package com.alhkam.film_web.rating_api.facade;

import com.alhkam.film_web.rating_api.dto.RatingAverageResponseDTO;
import com.alhkam.film_web.rating_api.dto.RatingDetailsDTO;
import com.alhkam.film_web.rating_api.dto.RatingRequestDTO;
import com.alhkam.film_web.rating_api.dto.RatingResponseDTO;

public interface RatingApiFacade {
  RatingAverageResponseDTO getFilmRatingAverage(Long filmId);

  RatingResponseDTO createRating(RatingRequestDTO ratingRequestDTO);

  RatingDetailsDTO getRatingAndCreationTime(Long filmId, Long userId);
}
