package com.alhkam.film_api.rest.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
  String createToken(UserDetails userDetails);

  long getExpirationInSeconds();
}
