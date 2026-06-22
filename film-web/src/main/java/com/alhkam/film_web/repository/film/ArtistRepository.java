package com.alhkam.film_web.repository.film;

import com.alhkam.film_web.domain.film.Artist;
import com.alhkam.film_web.domain.film.ArtistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

  List<Artist> findByType(ArtistType type);
}
