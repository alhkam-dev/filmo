package com.alhkam.film_web.repository;

import com.alhkam.film_web.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

  boolean existsByUserIdAndFilmId(Long userId, Long filmId);

  @Query("SELECT AVG(r.score) FROM Rating r WHERE r.filmId = :filmId")
  Optional<Double> getAverageScoreByFilmId(@Param("filmId") Long filmId);
}
