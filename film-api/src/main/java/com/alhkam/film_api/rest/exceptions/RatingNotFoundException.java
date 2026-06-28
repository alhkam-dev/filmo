package com.alhkam.film_api.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RatingNotFoundException extends RuntimeException {

  public RatingNotFoundException() {
    super();
  }

  public RatingNotFoundException(String message) {
    super(message);
  }

  public RatingNotFoundException(long id) {
    super("Rating with id: '" + id + "' not found");
  }

  public RatingNotFoundException(long filmId, long userId) {
    super("Rating for filmId '" + filmId + "' and userId '" + userId + "' not found");
  }
}
