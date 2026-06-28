package com.alhkam.film_web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingFormDTO {

    @NotNull(message = "{film.rating.score.notNull}")
    @Min(value = 1, message = "{film.rating.score.range}")
    @Max(value = 5, message = "{film.rating.score.range}")
    private Integer score;
}
