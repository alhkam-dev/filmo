package com.alhkam.film_web.service;

import com.alhkam.film_web.domain.Artist;
import com.alhkam.film_web.domain.ArtistType;
import com.alhkam.film_web.dto.ArtistCreationDTO;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ArtistService {

  boolean existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(String name, String surname, ArtistType type);

  boolean createArtist(ArtistCreationDTO artistCreationDTO);

  List<Artist> findArtistsByType(ArtistType artistType);
}
