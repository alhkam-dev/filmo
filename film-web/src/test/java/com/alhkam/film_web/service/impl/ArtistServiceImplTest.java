package com.alhkam.film_web.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.alhkam.film_web.domain.Artist;
import com.alhkam.film_web.domain.ArtistType;
import com.alhkam.film_web.dto.ArtistCreationDTO;
import com.alhkam.film_web.exception.ArtistAlreadyExistsException;
import com.alhkam.film_web.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para ArtistServiceImpl")
class ArtistServiceImplTest {

  @Mock private ArtistRepository artistRepository;

  @InjectMocks private ArtistServiceImpl artistService;

  private ArtistCreationDTO validArtistCreationDTO;

  @BeforeEach
  void setUp() {
    validArtistCreationDTO =
        ArtistCreationDTO.builder()
            .name("Martin")
            .surname("Scorsese")
            .type(ArtistType.DIRECTOR)
            .build();
  }

  @Nested
  @DisplayName("Tests para createArtist")
  class CreateArtistTest {

    @Test
    @DisplayName("Crea artista correctamente al usar datos validos y cuando este no existe")
    void givenValidArtistCreationDTO_whenCreateArtist_thenSavesArtistAndReturnTrue() {
      when(artistRepository.existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(
              "Martin", "Scorsese", ArtistType.DIRECTOR))
          .thenReturn(false);

      // Usamos un captor para inspeccionar que objeto se le envía al save del repository
      ArgumentCaptor<Artist> artistCaptor = ArgumentCaptor.forClass(Artist.class);

      boolean result = artistService.createArtist(validArtistCreationDTO);

      assertThat(result).isTrue();

      verify(artistRepository, times(1)).save(artistCaptor.capture());

      Artist savedArtist = artistCaptor.getValue();
      assertThat(savedArtist.getName()).isEqualTo("Martin");
      assertThat(savedArtist.getSurname()).isEqualTo("Scorsese");
      assertThat(savedArtist.getType()).isEqualTo(ArtistType.DIRECTOR);
    }

    @Test
    @DisplayName("Lanza ArtistAlreadyExistsException si el artista ya está registrado")
    void givenExistingArtistDTO_whenCreateArtist_thenThrowsArtistAlreadyExistsException() {
      when(artistRepository.existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(any(), any(), any()))
          .thenReturn(true);

      assertThatThrownBy(() -> artistService.createArtist(validArtistCreationDTO))
          .isInstanceOf(ArtistAlreadyExistsException.class)
          .hasMessage("Artist has already been created");

      verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    @DisplayName("Lanza IllegalArgumentException si el DTO de entrada es nulo")
    void givenNullDTO_whenCreateArtist_thenThrowsIllegalArgumentException() {
      assertThatThrownBy(() -> artistService.createArtist(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Artist creation data cannot be null");
    }

    @Test
    @DisplayName("Lanza IllegalArgumentException si el nombre o apellido están en blanco")
    void givenBlankFields_whenCreateArtist_thenThrowsIllegalArgumentException() {
      ArtistCreationDTO invalidDto =
          ArtistCreationDTO.builder()
              .name(" ")
              .surname("Scorsese")
              .type(ArtistType.DIRECTOR)
              .build();

      assertThatThrownBy(() -> artistService.createArtist(invalidDto))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Name and Surname are strictly required");
    }
  }
}
