package com.alhkam.film_web.security;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .authorizeHttpRequests(
            auth ->
                auth.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR)
                    .permitAll()
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**")
                    .permitAll()
                    .requestMatchers("/", "/index", "/login", "/register", "/error")
                    .permitAll()
                    .requestMatchers(
                        "/filmo/films/films-create",
                        "/filmo/films/films-edit",
                        "/filmo/artists/artists-create")
                    .hasRole("ADMIN")
                    .requestMatchers("/filmo/**")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exceptions ->
                exceptions.authenticationEntryPoint(
                    (request, response, authException) -> {
                      String uri = request.getRequestURI();

                      // Si el usuario intentaba ir a la zona privada de la app, le mandamos al
                      // login
                      if (uri.startsWith("/filmo")) {
                        response.sendRedirect(request.getContextPath() + "/login");
                      } else {
                        // Si ha escrito cualquier otra ruta que no existe, forzamos error 404
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                      }
                    }))
        .formLogin(
            form ->
                form.loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/filmo", true)
                    .failureUrl("/login?error=true")
                    .permitAll())
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll())
        .csrf(Customizer.withDefaults())
        .cors(Customizer.withDefaults())
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
