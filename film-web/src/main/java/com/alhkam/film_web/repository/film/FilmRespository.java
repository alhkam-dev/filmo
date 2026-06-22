package com.alhkam.film_web.repository.film;

import com.alhkam.film_web.domain.film.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRespository extends JpaRepository<Film, Long> {

    // En SQL utiliza un LIKE y así evitar utilizar comodines y
    // también que no interfieran las mayúsculas y minúsculas
    List<Film> findByTitleContainingIgnoreCase(String title);
}
