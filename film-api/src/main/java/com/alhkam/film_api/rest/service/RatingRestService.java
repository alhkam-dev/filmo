package com.alhkam.film_api.rest.service;

import com.alhkam.film_api.rest.dto.RatingAverageResponseDTO;
import com.alhkam.film_api.rest.dto.RatingDetailsDTO;
import com.alhkam.film_api.rest.dto.RatingRequestDTO;
import com.alhkam.film_api.rest.dto.RatingResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface RatingRestService {
  RatingResponseDTO saveRating(RatingRequestDTO ratingRequestDTO);

  RatingDetailsDTO getRatingAndCreationTime(Long filmId, Long userId);

  RatingAverageResponseDTO getFilmAverageAndCount(Long filmId);
}
