package com.alhkam.film_api.rest.service.impl;

import com.alhkam.film_api.rest.service.JwtService;
import com.alhkam.film_api.security.properties.JwtConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

  private final JwtConfigurationProperties jwtConfigurationProperties;
  private final NimbusJwtEncoder nimbusJwtEncoder;

  public String createToken(UserDetails userDetails) {
    Instant now = Instant.now();
    Duration duration = Duration.parse(jwtConfigurationProperties.duration());

    JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

    List<String> authorities =
        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(now.plus(duration))
            .subject(userDetails.getUsername())
            .claim("authorities", authorities)
            .build();

    JwtEncoderParameters encoderParameters = JwtEncoderParameters.from(jwsHeader, claims);

    return nimbusJwtEncoder.encode(encoderParameters).getTokenValue();
  }

  public long getExpirationInSeconds() {
    return Duration.parse(jwtConfigurationProperties.duration()).getSeconds();
  }
}
