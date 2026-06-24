package com.alhkam.film_web.service;

import com.alhkam.film_web.domain.ArtistType;
import com.alhkam.film_web.dto.ArtistCreationDTO;

public interface ArtistService {

  boolean existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(String name, String surname, ArtistType type);

  boolean createArtist(ArtistCreationDTO artistCreationDTO);
}
