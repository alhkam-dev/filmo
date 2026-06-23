package com.alhkam.film_web.dto;

import com.alhkam.film_web.validation.ValidDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserRegisterDTO(
    Long id,

    @NotBlank(message = "{register.validation.username.notblank}")
    String username,

    @NotBlank(message = "{register.validation.password.notblank}")
    String password,

    @NotBlank(message = "{register.validation.passwordConfirm.notblank}")
    String passwordConfirm,

    @NotBlank(message = "{register.validation.email.notblank}")
    @Email(message = "{register.validation.email.format}")
    String email,

    @NotBlank(message = "{register.validation.name.notblank}")
    String name,

    @NotBlank(message = "{register.validation.surname.notblank}")
    String surname,

    @NotNull(message = "{register.validation.dateOfBirth.notblank}")
    @ValidDate(message = "{register.validation.dateOfBirth.format}")
    String dateOfBirth
) {}
