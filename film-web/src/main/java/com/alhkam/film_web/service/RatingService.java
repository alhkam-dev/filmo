package com.alhkam.film_web.service;

public interface RatingService {

  void saveRating(Long filmId, Integer score, Long userId);

  Integer getUserRatingForFilm(Long userId, Long filmId);
}
