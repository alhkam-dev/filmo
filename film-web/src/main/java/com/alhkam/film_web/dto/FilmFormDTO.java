package com.alhkam.film_web.dto;

import com.alhkam.film_web.validation.OnCreate;
import com.alhkam.film_web.validation.ValidImage;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record FilmFormDTO(
    Long id,
    @NotBlank(message = "{film.validation.title.notBlank}")
    String title,

    @NotNull(message = "{film.validation.releaseYear.notnull}")
    @Min(value = 1, message = "{film.validation.releaseYear.min}")
    @Digits(integer = 4, fraction = 0, message = "{film.validation.releaseYear.digits}")
    Integer releaseYear,

    @NotNull(message = "{film.validation.director.notBlank}")
    Long directorId,

    @NotEmpty(message = "{film.validation.artists.min}")
    List<Long> actorIds,

    @NotNull(groups = OnCreate.class, message = "{film.validation.poster.notNull}")
    @ValidImage(groups = OnCreate.class, message = "{film.validation.poster.validImage}")
    MultipartFile poster
) {}
