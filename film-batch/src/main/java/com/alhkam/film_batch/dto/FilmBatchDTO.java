package com.alhkam.film_batch.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmBatchDTO {
  private Long id;
  private String title;
  private int releaseYear;
}
