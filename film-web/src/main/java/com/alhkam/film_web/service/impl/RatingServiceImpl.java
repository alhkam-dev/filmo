package com.alhkam.film_web.service.impl;

import com.alhkam.film_web.domain.Rating;
import com.alhkam.film_web.repository.RatingRepository;
import com.alhkam.film_web.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

  private final RatingRepository ratingRepository;

  @Override
  @Transactional(readOnly = true)
  public Integer getUserRatingForFilm(Long userId, Long filmId) {
    return ratingRepository.findScoreByUserIdAndFilmId(userId, filmId).orElse(null);
  }

  @Override
  @Transactional
  public void saveRating(Long filmId, Integer score, Long userId) {
    if (getUserRatingForFilm(userId, filmId) != null) {
      throw new IllegalStateException("User has already rated the film.");
    }

    Rating rating = Rating.builder().filmId(filmId).userId(userId).score(score).build();

    ratingRepository.save(rating);
  }
}
