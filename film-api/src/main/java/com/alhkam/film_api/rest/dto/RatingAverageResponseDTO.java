package com.alhkam.film_api.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingAverageResponseDTO {
  private double average;
  private long ratings;
}
