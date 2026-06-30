package com.alhkam.film_api.rest.controller;

import com.alhkam.film_api.rest.dto.TokenResponseDTO;
import com.alhkam.film_api.rest.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authenticate")
@Tag(
    name = "Authentication",
    description = "Endpoints for client authentication and token generation")
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  @Operation(
      summary = "Authenticate client",
      description =
          "Generates a JWT access token for external clients using OAuth2 client credentials flow with Basic Auth.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token successfully generated",
            content = @Content(schema = @Schema(implementation = TokenResponseDTO.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid or missing grant_type"),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid client credentials (Basic Auth)")
      })
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> postAuthenticate(
      @RequestParam(value = "grant_type", required = false) String grantType,
      Authentication authentication) {

    if (!"client_credentials".equals(grantType)) {
      return ResponseEntity.badRequest().body("Invalid grant_type");
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    TokenResponseDTO tokenResponseDTO = authenticationService.generateClientToken(userDetails);

    return ResponseEntity.ok(tokenResponseDTO);
  }
}
