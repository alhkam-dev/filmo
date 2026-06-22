package com.alhkam.film_web.repository.film;

import static org.assertj.core.api.Assertions.assertThat;

import com.alhkam.film_web.domain.Rating;
import java.util.List;
import java.util.Optional;

import com.alhkam.film_web.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;",
      "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
class RatingRepositoryTest {

  @Autowired private RatingRepository ratingRepository;

  @BeforeEach
  void beforeEach() {
    Long filmId = 42L;

    Rating rating1 = Rating.builder().filmId(filmId).userId(1L).score(4).build();
    Rating rating2 = Rating.builder().filmId(filmId).userId(2L).score(3).build();
    Rating rating3 = Rating.builder().filmId(filmId).userId(3L).score(5).build();
    Rating ratingDifferentFilm = Rating.builder().filmId(99L).userId(2L).score(1).build();

    ratingRepository.saveAll(List.of(rating1, rating2, rating3, ratingDifferentFilm));
  }

  @Test
  void givenRatingsForFilm_whenGetAverageScoreByFilmId_thenReturnCorrectAverage() {
    Optional<Double> avgRating = ratingRepository.getAverageScoreByFilmId(42L);

    assertThat(avgRating).isPresent();
    assertThat(avgRating.get()).isEqualTo(4.0);
  }
}
