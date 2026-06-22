package com.alhkam.film_web.repository;

import com.alhkam.film_web.domain.Poster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PosterRepository extends JpaRepository<Poster, Long> {

  Optional<Poster> findByResourceId(UUID resourceId);
}
