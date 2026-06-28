package com.alhkam.film_api.rest.controller;

import com.alhkam.film_api.rest.dto.ErrorResponseDTO;
import com.alhkam.film_api.rest.dto.RatingAverageResponseDTO;
import com.alhkam.film_api.rest.service.RatingRestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ratings-average")
@Tag(name = "Ratings", description = "Endpoints for managing film ratings and scores")
public class RatingAverageController {

  private final RatingRestService ratingRestService;

  @GetMapping(value = "/films/{filmId}", produces = "application/json")
  @Operation(
      summary = "Get film ratings average",
      description = "Retrieves the average score and total number of ratings for a film.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Average successfully calculated",
            content = @Content(schema = @Schema(implementation = RatingAverageResponseDTO.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or expired JWT token",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
      })
  public ResponseEntity<RatingAverageResponseDTO> getFilmAverageAndCount(
      @PathVariable Long filmId) {
    RatingAverageResponseDTO ratingAverageResponseDTO =
        ratingRestService.getFilmAverageAndCount(filmId);

    return ResponseEntity.ok(ratingAverageResponseDTO);
  }
}
