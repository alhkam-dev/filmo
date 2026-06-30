package com.alhkam.film_web.service.impl;

import com.alhkam.film_web.domain.Artist;
import com.alhkam.film_web.domain.Film;
import com.alhkam.film_web.domain.Poster;
import com.alhkam.film_web.domain.User;
import com.alhkam.film_web.dto.FilmDetailsDTO;
import com.alhkam.film_web.dto.FilmFormDTO;
import com.alhkam.film_web.dto.FilmPreviewDTO;
import com.alhkam.film_web.rating_api.dto.RatingAverageResponseDTO;
import com.alhkam.film_web.rating_api.facade.RatingApiFacade;
import com.alhkam.film_web.repository.*;
import com.alhkam.film_web.service.FilmService;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

  private final FilmRespository filmRespository;
  private final PosterRepository posterRepository;
  private final ArtistRepository artistRepository;
  private final UserRespository userRespository;
  private final RatingApiFacade ratingApiFacade;

  @Value("${filmo.posters.upload-dir}")
  private String uploadDir;

  @Override
  @Transactional
  public void saveFilm(FilmFormDTO filmFormDTO) {
    Poster poster = storePosterInStorageAndDatabase(filmFormDTO.poster());

    Artist director =
        artistRepository
            .findById(filmFormDTO.directorId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Director with id: " + filmFormDTO.directorId() + " not found"));

    List<Artist> actorsList = artistRepository.findAllById(filmFormDTO.actorIds());

    Set<Artist> actors = new HashSet<>(actorsList);

    User currentUser = getCurrentUser();

    Film film =
        Film.builder()
            .title(filmFormDTO.title())
            .releaseYear(filmFormDTO.releaseYear())
            .director(director)
            .actors(actors)
            .poster(poster)
            .createdBy(currentUser)
            .build();

    filmRespository.save(film);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FilmPreviewDTO> searchFilmsByTitle(String searchText) {
    List<Film> filmsFound;

    if (searchText == null || searchText.isBlank()) {
      filmsFound = filmRespository.findAll();
    } else {
      filmsFound = filmRespository.findByTitleContainingIgnoreCase(searchText);
    }

    return filmsFound.stream()
        .map(
            film ->
                FilmPreviewDTO.builder()
                    .id(film.getId())
                    .title(film.getTitle())
                    .releaseYear(film.getReleaseYear())
                    .posterResourceId(
                        film.getPoster() != null ? film.getPoster().getResourceId() : null)
                    .build())
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public FilmFormDTO findById(Long id) {
    Film film =
        filmRespository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Could not find film with id:" + id));

    List<Long> actorIds = film.getActors().stream().map(Artist::getId).toList();

    return FilmFormDTO.builder()
        .id(film.getId())
        .title(film.getTitle())
        .releaseYear(film.getReleaseYear())
        .directorId(film.getDirector().getId())
        .actorIds(actorIds)
        .build();
  }

  @Override
  @Transactional
  public void updateFilm(Long id, FilmFormDTO filmFormDTO) {
    Film film =
        filmRespository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Could not find film with id:" + id));

    film.setTitle(filmFormDTO.title());
    film.setReleaseYear(filmFormDTO.releaseYear());

    // Gestión del director
    Artist director =
        artistRepository
            .findById(filmFormDTO.directorId())
            .orElseThrow(() -> new EntityNotFoundException("Director not found"));
    film.setDirector(director);

    // Gestión actores
    if (filmFormDTO.actorIds() != null && !filmFormDTO.actorIds().isEmpty()) {
      List<Artist> filmActors = artistRepository.findAllById(filmFormDTO.actorIds());
      film.setActors(new HashSet<>(filmActors));
    } else {
      film.getActors().clear();
    }

    // Gestión poster
    if (filmFormDTO.poster() != null && !filmFormDTO.poster().isEmpty()) {
      Poster newPoster = storePosterInStorageAndDatabase(filmFormDTO.poster());
      Poster oldPoster = film.getPoster();

      film.setPoster(newPoster);

      if (oldPoster != null) {
        try {
          Path targetDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
          Path oldFileLocation = targetDirectory.resolve(oldPoster.getResourceId().toString());

          Files.deleteIfExists(oldFileLocation);
        } catch (IOException e) {
          log.warn(
              "Could not delete the physical file of the previous poster with id: {}. {}",
              oldPoster.getResourceId(),
              e.getMessage());
        }

        posterRepository.delete(oldPoster);
      }
    }

    filmRespository.save(film);
  }

  @Override
  @Transactional(readOnly = true)
  public FilmDetailsDTO getFilmDetails(Long id) {
    Film film =
        filmRespository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Could not find film with id: " + id));

    String directorFullName = film.getDirector().getName() + " " + film.getDirector().getSurname();

    Set<String> actorsFullNames =
        film.getActors().stream()
            .map(actor -> actor.getName() + " " + actor.getSurname())
            .collect(Collectors.toSet());

    UUID posterResourceId = film.getPoster() != null ? film.getPoster().getResourceId() : null;

    String avgRating = "--";
    try {
      RatingAverageResponseDTO avgResponse = ratingApiFacade.getFilmRatingAverage(id);

      if (avgResponse != null && avgResponse.ratings() > 0) {
        avgRating =
            BigDecimal.valueOf(avgResponse.average())
                .setScale(2, RoundingMode.CEILING)
                .toString();
      }
    } catch (Exception e) {
      log.warn("Could not retrieve average rating from film-api for filmId: {}", id, e);
    }

    return FilmDetailsDTO.builder()
        .id(film.getId())
        .title(film.getTitle())
        .releaseYear(film.getReleaseYear())
        .directorFullName(directorFullName)
        .actorsFullNames(actorsFullNames)
        .posterResourceId(posterResourceId)
        .averageRating(avgRating)
        .build();
  }

  private Poster storePosterInStorageAndDatabase(MultipartFile file) {
    UUID resourceId = UUID.randomUUID();

    String filename = file.getOriginalFilename();

    try {
      // Asegura que el directorio existe en la máquina
      Path targetDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
      Files.createDirectories(targetDirectory);

      // Ruta final del archivo: directorio + resourceId
      Path targetLocation = targetDirectory.resolve(resourceId.toString());

      Files.copy(file.getInputStream(), targetLocation);
    } catch (IOException e) {
      throw new RuntimeException("Could not save the poster file. ", e);
    }

    Poster poster =
        Poster.builder()
            .resourceId(resourceId)
            .filename(filename)
            .contentType(file.getContentType())
            .size((int) file.getSize())
            .build();

    return posterRepository.save(poster);
  }

  private User getCurrentUser() {
    Object principal =
        Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
            .getPrincipal();

    String username;

    if (principal instanceof UserDetails userDetails) {
      username = userDetails.getUsername();
    } else {
      username = principal.toString();
    }

    return userRespository
        .findByUsername(username)
        .orElseThrow(() -> new IllegalStateException("Session user not found in the system"));
  }
}
