package com.alhkam.film_web.repository;

import com.alhkam.film_web.domain.Artist;
import com.alhkam.film_web.domain.ArtistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

  boolean existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(
      String name, String surname, ArtistType type);

  List<Artist> findByType(ArtistType type);
}
