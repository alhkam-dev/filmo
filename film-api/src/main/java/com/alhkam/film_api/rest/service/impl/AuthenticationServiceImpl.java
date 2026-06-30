package com.alhkam.film_api.rest.service.impl;

import com.alhkam.film_api.rest.dto.TokenResponseDTO;
import com.alhkam.film_api.rest.service.AuthenticationService;
import com.alhkam.film_api.rest.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final JwtService jwtService;

  public TokenResponseDTO generateClientToken(UserDetails userDetails) {
    String token = jwtService.createToken(userDetails);

    long expiresIn = jwtService.getExpirationInSeconds();

    return TokenResponseDTO.builder()
        .accessToken(token)
        .tokenType("bearer")
        .expiresIn(expiresIn)
        .build();
  }
}
