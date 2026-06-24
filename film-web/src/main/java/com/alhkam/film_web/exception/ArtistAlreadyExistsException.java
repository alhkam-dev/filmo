package com.alhkam.film_web.exception;

public class ArtistAlreadyExistsException extends RuntimeException {
  public ArtistAlreadyExistsException(String message) {
    super(message);
  }
}
