package com.alhkam.film_web.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDTO {
  private String url;
  private String exception;
  private String motive;
  private String method;
}
