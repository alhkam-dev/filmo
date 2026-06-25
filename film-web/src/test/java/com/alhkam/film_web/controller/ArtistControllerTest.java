package com.alhkam.film_web.controller;

import com.alhkam.film_web.domain.ArtistType;
import com.alhkam.film_web.dto.ArtistCreationDTO;
import com.alhkam.film_web.exception.ArtistAlreadyExistsException;
import com.alhkam.film_web.service.ArtistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArtistController.class)
@DisplayName("Tests para ArtistController")
class ArtistControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ArtistService artistService;

  private ArtistCreationDTO validArtistCreationDTO;

  @BeforeEach
  void setUp() {
    validArtistCreationDTO =
        ArtistCreationDTO.builder()
            .name("Christopher")
            .surname("Nolan")
            .type(ArtistType.DIRECTOR)
            .build();
  }

  @Nested
  @DisplayName("Tests para GET /artists/artists-create")
  class GetArtistCreateTest {

    @Test
    @DisplayName("Muestra el formulario de creación de artista vacío")
    void givenGetCreateRequest_whenShowForm_thenStatusOkAndReturnCreateViewWithEmptyDto()
        throws Exception {
      MvcResult result =
          mockMvc
              .perform(get("/artists/artists-create"))
              .andExpect(status().isOk())
              .andExpect(view().name("filmo/artists/artists-create"))
              .andExpect(model().attributeExists("artistCreationDTO"))
              .andReturn();

      ArtistCreationDTO dto =
          (ArtistCreationDTO)
              Objects.requireNonNull(result.getModelAndView()).getModel().get("artistCreationDTO");

      assertAll(
          "Propiedades del DTO de creación de artista vacío",
          () -> assertNull(dto.name()),
          () -> assertNull(dto.surname()),
          () -> assertNull(dto.type()));
    }
  }

  @Nested
  @DisplayName("Tests para POST /artists/artists-create")
  class PostArtistCreateTest {

    @Test
    @DisplayName("Crea un artista válido correctamente y redirige con mensaje de éxito")
    void givenValidArtistCreationDTO_whenCreateArtist_thenRedirectWithFlashAttribute()
        throws Exception {
      mockMvc
          .perform(
              post("/artists/artists-create")
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                  .flashAttr("artistCreationDTO", validArtistCreationDTO))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/artists/artists-create"))
          .andExpect(flash().attribute("artistCreationSuccesMessage", true));

      verify(artistService).createArtist(any(ArtistCreationDTO.class));
    }

    @Test
    @DisplayName("Falla la creación si el artista ya existe en el sistema")
    void givenDuplicateArtist_whenCreateArtist_thenReturnsFormWithAlreadyExistsError()
        throws Exception {

      doThrow(new ArtistAlreadyExistsException("Artist has already been created"))
          .when(artistService)
          .createArtist(any(ArtistCreationDTO.class));

      mockMvc
          .perform(
              post("/artists/artists-create")
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                  .flashAttr("artistCreationDTO", validArtistCreationDTO))
          .andExpect(status().isOk()) // Al fallar vuelve a pintar la misma pantalla
          .andExpect(view().name("filmo/artists/artists-create"))
          .andExpect(model().attribute("artistAlreadyExistsError", true));
    }

    @Test
    @DisplayName("Falla la creación si los campos no cumplen las validaciones automáticas (@Valid)")
    void givenInvalidFormFields_whenCreateArtist_thenReturnsFormWithBindingErrors()
        throws Exception {
      mockMvc
          .perform(
              post("/artists/artists-create")
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                  // Forzamos campos inválidos usando params uno a uno para activar el DataBinder
                  .param("name", "") // Nombre vacío viola @NotBlank/@NotEmpty
                  .param("surname", "Spielberg")
                  .param("type", "DIRECTOR"))
          .andExpect(status().isOk())
          .andExpect(view().name("filmo/artists/artists-create"))
          .andExpect(model().hasErrors())
          .andExpect(model().attributeHasFieldErrors("artistCreationDTO", "name"));
    }
  }
}
