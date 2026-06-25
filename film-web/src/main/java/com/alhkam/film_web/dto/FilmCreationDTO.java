package com.alhkam.film_web.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record FilmCreationDTO(
    @NotBlank(message = "{film.validation.title.notblank}") String title,

    @NotNull(message = "{film.validation.releaseyear.notnull}")
    @Min(value = 1, message = "{film.validation.releaseyear.min}")
    @Digits(integer = 4, fraction = 0, message = "{film.validation.releaseyear.digits}")
    Integer releaseYear,

    @NotNull(message = "{film.validation.director.notnull}")
    Long directorId,

    @NotEmpty(message = "{film.validation.artists.min}")
    List<Long> actorIds,

    @NotNull(message = "{film.validation.poster.notnull}")
    MultipartFile poster
) {}
