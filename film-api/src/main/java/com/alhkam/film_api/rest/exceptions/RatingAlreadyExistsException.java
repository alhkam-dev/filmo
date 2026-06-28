package com.alhkam.film_api.rest.exceptions;

public class RatingAlreadyExistsException extends RuntimeException {
  public RatingAlreadyExistsException(String message) {
    super(message);
  }

  public RatingAlreadyExistsException(Long userId, Long filmId) {
    super(String.format("User %d has already rated film %d", userId, filmId));
  }
}
