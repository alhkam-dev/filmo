package com.alhkam.film_web.service.impl;

import com.alhkam.film_web.domain.Artist;
import com.alhkam.film_web.domain.ArtistType;
import com.alhkam.film_web.dto.ArtistCreationDTO;
import com.alhkam.film_web.exception.ArtistAlreadyExistsException;
import com.alhkam.film_web.repository.ArtistRepository;
import com.alhkam.film_web.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

  private final ArtistRepository artistRepository;

  @Override
  @Transactional(readOnly = true)
  public boolean existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(
      String name, String surname, ArtistType type) {
    return artistRepository.existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(name, surname, type);
  }

  @Override
  @Transactional
  public boolean createArtist(ArtistCreationDTO artistCreationDTO) {
    if (artistCreationDTO == null) {
      throw new IllegalArgumentException("Artist creation data cannot be null");
    }

    if (artistCreationDTO.name() == null
        || artistCreationDTO.name().isBlank()
        || artistCreationDTO.surname() == null
        || artistCreationDTO.surname().isBlank()) {
      throw new IllegalArgumentException("Name and Surname are strictly required");
    }

    if (artistRepository.existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(
        artistCreationDTO.name(), artistCreationDTO.surname(), artistCreationDTO.type())) {
      throw new ArtistAlreadyExistsException("Artist has already been created");
    }

    Artist artistToCreate = new Artist();
    artistToCreate.setName(artistCreationDTO.name().trim());
    artistToCreate.setSurname(artistCreationDTO.surname().trim());
    artistToCreate.setType(artistCreationDTO.type());

    artistRepository.save(artistToCreate);
    return true;
  }

  @Override
  public List<Artist> findArtistsByType(ArtistType artistType) {
    return artistRepository.findByTypeOrderBySurnameAscNameAsc(artistType);
  }
}
