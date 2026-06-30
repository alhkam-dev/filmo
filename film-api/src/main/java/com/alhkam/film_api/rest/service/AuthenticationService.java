package com.alhkam.film_api.rest.service;

import com.alhkam.film_api.rest.dto.TokenResponseDTO;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
  TokenResponseDTO generateClientToken(UserDetails userDetails);
}
