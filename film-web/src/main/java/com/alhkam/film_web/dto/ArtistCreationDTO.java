package com.alhkam.film_web.dto;

import com.alhkam.film_web.domain.ArtistType;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ArtistCreationDTO(
    Long id,

    @NotBlank(message = "{artist.creation.name.notblank}")
    String name,

    @NotBlank(message = "{artist.creation.surname.notblank}")
    String surname,

    ArtistType type
) {}
