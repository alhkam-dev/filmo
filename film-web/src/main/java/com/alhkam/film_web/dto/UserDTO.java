package com.alhkam.film_web.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record UserDTO(
    Long id,
    String username,
    String email,
    String name,
    String surname,
    LocalDate dateOfBirth,
    LocalDateTime created,
    Set<String> roles) {}
