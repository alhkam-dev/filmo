package com.alhkam.film_api.persistence.repository;

import com.alhkam.film_api.persistence.entity.RatingEntity;
import com.alhkam.film_api.rest.dto.RatingAverageResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {

  boolean existsByUserIdAndFilmId(Long userId, Long filmId);

  Optional<RatingEntity> findByFilmIdAndUserId(Long filmId, Long userId);

  @Query("SELECT r.score FROM RatingEntity r WHERE r.userId = :userId AND r.filmId = :filmId")
  Optional<Integer> findScoreByUserIdAndFilmId(
      @Param("userId") Long userId, @Param("filmId") Long filmId);

  List<RatingEntity> findByFilmId(Long filmId);
}
