package com.alhkam.film_web.repository;

import com.alhkam.film_web.domain.Artist;
import com.alhkam.film_web.domain.ArtistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

  @Query("SELECT COUNT(a) > 0 FROM Artist a WHERE " +
          "LOWER(a.name) = LOWER(:name) AND " +
          "LOWER(a.surname) = LOWER(:surname) AND " +
          "(:type IS NULL AND a.type IS NULL OR a.type = :type)")
  boolean existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(
          @Param("name") String name,
          @Param("surname") String surname,
          @Param("type") ArtistType type
  );

  List<Artist> findByType(ArtistType type);

  List<Artist> findByTypeOrderBySurnameAscNameAsc(ArtistType type);
}
