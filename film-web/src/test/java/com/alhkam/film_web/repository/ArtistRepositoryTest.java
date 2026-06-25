package com.alhkam.film_web.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.alhkam.film_web.domain.Artist;
import com.alhkam.film_web.domain.ArtistType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;",
      "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
class ArtistRepositoryTest {

  @Autowired private ArtistRepository artistRepository;

  @BeforeEach
  void setUp() {
    Artist artist =
        Artist.builder().name("Martin").surname("Scorsese").type(ArtistType.DIRECTOR).build();

    artistRepository.save(artist);
  }

  @Test
  @DisplayName("Retorna TRUE cuando el nombre y apellido coinciden exactamente")
  void givenExistingArtist_whenCheckExistence_thenReturnTrue() {
    boolean exists =
        artistRepository.existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(
            "Martin", "Scorsese", ArtistType.DIRECTOR);

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Retorna TRUE ignorando si se escribe en mayúsculas o minúsculas")
  void givenExistingArtistsRegardlessOfCase_whenCheckExistence_thenReturnTrue() {
    boolean existsLower =
        artistRepository.existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(
            "martin", "scorsese", ArtistType.DIRECTOR);
    boolean existsUpper =
        artistRepository.existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(
            "MARTIN", "SCORSESE", ArtistType.DIRECTOR);

    assertThat(existsLower).isTrue();
    assertThat(existsUpper).isTrue();
  }

  @Test
  @DisplayName("Retorna FALSE cuando el artista no existe en la base de datos")
  void givenNonExistingArtist_whenCheckExistence_thenReturnFalse() {
    boolean exists =
        artistRepository.existsByNameIgnoreCaseAndSurnameIgnoreCaseAndType(
            "Leo", "Messi", ArtistType.ACTOR);

    assertThat(exists).isFalse();
  }
}
