package com.alhkam.film_api.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new film rating")
public class RatingRequestDTO {

  @NotNull(message = "UserId is required")
  @Positive(message = "UserId must be a positive number")
  @Schema(
      description = "Unique identifier of the user",
      example = "2",
      requiredMode = Schema.RequiredMode.REQUIRED)
  Long userId;

  @NotNull(message = "FilmId is required")
  @Positive(message = "FilmId must be a positive number")
  @Schema(
      description = "Unique identifier of the film",
      example = "101",
      requiredMode = Schema.RequiredMode.REQUIRED)
  Long filmId;

  @NotNull(message = "Score is required")
  @Min(value = 1, message = "Score must be at least 1")
  @Max(value = 5, message = "Score cannot be greater than 5")
  @Schema(
      description = "Rating score between 1 and 5",
      example = "5",
      minimum = "1",
      maximum = "5",
      requiredMode = Schema.RequiredMode.REQUIRED)
  Integer score;
}
