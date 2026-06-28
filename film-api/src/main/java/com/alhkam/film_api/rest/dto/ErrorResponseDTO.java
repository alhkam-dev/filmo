package com.alhkam.film_api.rest.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Value
@Builder
@Jacksonized
public class ErrorResponseDTO {
    LocalDateTime timestamp;
    int status;
    String error;
    String message;
}
