package com.alhkam.film_api.rest.controller;

import com.alhkam.film_api.rest.dto.ErrorResponseDTO;
import com.alhkam.film_api.rest.dto.RatingDetailsDTO;
import com.alhkam.film_api.rest.dto.RatingRequestDTO;
import com.alhkam.film_api.rest.dto.RatingResponseDTO;
import com.alhkam.film_api.rest.service.RatingRestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ratings")
@Tag(name = "Ratings", description = "Endpoints for managing film ratings and scores")
public class RatingRestController {

  private final RatingRestService ratingRestService;

  @PostMapping(produces = "application/json", consumes = "application/json")
  @Operation(
      summary = "Create a new film rating",
      description = "Allows an authenticated user to submit a score between 1 and 5 for a film.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Rating successfully created"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data or rating already exists for this user and film",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or expired JWT token",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
      })
  public ResponseEntity<RatingResponseDTO> createRating(
      @Valid @RequestBody RatingRequestDTO ratingRequestDTO) {
    RatingResponseDTO ratingResponseDTO = ratingRestService.saveRating(ratingRequestDTO);

    return new ResponseEntity<>(ratingResponseDTO, HttpStatus.CREATED);
  }

  @GetMapping(value = "/films/{filmId}/users/{userId}", produces = "application/json")
  @Operation(
      summary = "Get a film rating and creation time",
      description = "Retrieves the score and creation time given by a user to a film.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Rating successfully retrieved",
            content = @Content(schema = @Schema(implementation = RatingDetailsDTO.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or expired JWT token",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found - The rating for the specified film and user does not exist",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
      })
  public ResponseEntity<RatingDetailsDTO> getRatingAndCreationTime(
      @PathVariable Long filmId, @PathVariable Long userId) {
    RatingDetailsDTO ratingDetailsDTO = ratingRestService.getRatingAndCreationTime(filmId, userId);
    return ResponseEntity.ok(ratingDetailsDTO);
  }
}
