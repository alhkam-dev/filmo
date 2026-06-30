package com.alhkam.film_api.security.config;

import java.util.List;

import com.alhkam.film_api.security.properties.SecurityConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityConfigurationProperties.class)
@RequiredArgsConstructor
public class SecurityConfig {

  private final NimbusJwtDecoder nimbusJwtDecoder;
  private final SecurityConfigurationProperties securityProperties;

  //Filtro Client Credentials
  @Bean
  @Order(1)
  public SecurityFilterChain authSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .securityMatcher("/authenticate")
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
  }

  // Filtro para Ratings
  @Bean
  @Order(2)
  public SecurityFilterChain jwtSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.POST, "/ratings")
                    .hasAuthority("write-resource")
                    .requestMatchers(HttpMethod.GET, "/ratings/films/**")
                    .hasAuthority("read-resource")
                    .requestMatchers(HttpMethod.GET, "/ratings-average/films/**")
                    .hasAuthority("read-resource")
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(
                    jwt ->
                        jwt.decoder(nimbusJwtDecoder)
                            .jwtAuthenticationConverter(new CustomeJwtAuthenticationConverter())))
        .build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    List<UserDetails> users =
        securityProperties.users().stream()
            .map(
                user ->
                    User.builder()
                        .username(user.username())
                        .password(user.password())
                        .authorities(user.authorities().toArray(new String[0]))
                        .build())
            .toList();

    return new InMemoryUserDetailsManager(users);
  }

  private static class CustomeJwtAuthenticationConverter
      implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
      JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
      jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
          jwt ->
              ((List<String>) jwt.getClaim("authorities"))
                  .stream().map(s -> (GrantedAuthority) new SimpleGrantedAuthority(s)).toList());
      return jwtAuthenticationConverter.convert(source);
    }
  }
}
