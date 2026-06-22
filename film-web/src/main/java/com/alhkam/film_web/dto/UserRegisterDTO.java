package com.alhkam.film_web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record UserRegisterDTO(
    Long id,

    @NotBlank(message = "must not be blank")
    String username,

    @NotBlank(message = "must not be blank")
    String password,

    @NotBlank(message = "must not be blank")
    String passwordConfirm,

    @NotBlank(message = "must not be blank")
    @Email(message = "must be a valid email adress")
    String email,

    @NotBlank(message = "must not be blank")
    String name,

    @NotBlank(message = "must not be blank")
    String surname,

    @NotNull(message = "must not be null")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    LocalDate dateOfBirth
) {}
