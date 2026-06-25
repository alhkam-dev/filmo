package com.alhkam.film_web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alhkam.film_web.dto.ArtistCreationDTO;
import com.alhkam.film_web.dto.UserRegisterDTO;
import com.alhkam.film_web.exception.UsernameAlreadyExistsException;
import com.alhkam.film_web.service.UserService;
import java.util.Objects;
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

@WebMvcTest(RegisterController.class)
@DisplayName("Tests para RegisterController")
class RegisterControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;

  private UserRegisterDTO validUserRegisterDTO;

  @BeforeEach
  void setUp() {
    validUserRegisterDTO =
        UserRegisterDTO.builder()
            .username("user")
            .email("user@filmo.com")
            .password("password")
            .passwordConfirm("password")
            .name("User")
            .surname("Filmo")
            .dateOfBirth("01/03/1995")
            .build();
  }

  @Nested
  @DisplayName("Tests para GET /register")
  class GetRegisterTest {

    @Test
    @DisplayName("Muestra el formulario de registro de usuario vacío")
    void givenGetRegisterRequest_whenShowRegisterForm_thenStatusOkAndReturnRegisterView()
        throws Exception {
      MvcResult result =
          mockMvc
              .perform(get("/register"))
              .andExpect(status().isOk())
              .andExpect(view().name("filmo/register"))
              .andExpect(model().attributeExists("userRegisterDTO"))
              .andReturn();

      UserRegisterDTO dto =
          (UserRegisterDTO)
              Objects.requireNonNull(result.getModelAndView()).getModel().get("userRegisterDTO");

      assertAll(
          "Propiedades del DTO de registro vacío",
          () -> assertNull(dto.username()),
          () -> assertNull(dto.email()),
          () -> assertNull(dto.password()),
          () -> assertNull(dto.passwordConfirm()),
          () -> assertNull(dto.name()),
          () -> assertNull(dto.surname()),
          () -> assertNull(dto.dateOfBirth()));
    }
  }

  @Nested
  @DisplayName("Tests para POST /register")
  class PostRegisterTest {

    @Test
    @DisplayName("Registra un usuario válido correctamente")
    void givenValidUserRegisterDTO_whenRegisterUser_thenStatusOk() throws Exception {
      mockMvc
          .perform(
              post("/register")
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                  .param("username", validUserRegisterDTO.username())
                  .param("email", validUserRegisterDTO.email())
                  .param("password", validUserRegisterDTO.password())
                  .param("passwordConfirm", validUserRegisterDTO.passwordConfirm())
                  .param("name", validUserRegisterDTO.name())
                  .param("surname", validUserRegisterDTO.surname())
                  .param("dateOfBirth", validUserRegisterDTO.dateOfBirth()))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/register"))
          .andExpect(flash().attribute("userRegistrationSuccesMessage", true));

      verify(userService).registerUser(any(UserRegisterDTO.class));
    }

    @Test
    @DisplayName("Falla el registro cuando las contraseñas no coinciden")
    void givenMismatchedPasswords_whenRegisterUser_thenShowsPasswordConfirmFieldError()
        throws Exception {
      UserRegisterDTO invalidPasswordDto =
          UserRegisterDTO.builder()
              .username(validUserRegisterDTO.username())
              .email(validUserRegisterDTO.email())
              .password(validUserRegisterDTO.password())
              .passwordConfirm("ContraseñaMal")
              .name(validUserRegisterDTO.name())
              .surname(validUserRegisterDTO.surname())
              .dateOfBirth(validUserRegisterDTO.dateOfBirth())
              .build();

      mockMvc
          .perform(
              post("/register")
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                  .flashAttr("userRegisterDTO", invalidPasswordDto))
          .andExpect(status().isOk())
          .andExpect(view().name("filmo/register"))
          .andExpect(model().hasErrors())
          .andExpect(model().attributeHasFieldErrors("userRegisterDTO", "passwordConfirm"));
    }

    @Test
    @DisplayName("Falla el registro cuando el username ya existe")
    void givenDuplicateUsername_whenRegisterUser_thenShowUsernameFieldError() throws Exception {
      String errorMessage = "The username is already taken";

      // Forzamos lanzar la excepción cuando reciba cualquier DTO
      doThrow(new UsernameAlreadyExistsException(errorMessage))
          .when(userService)
          .registerUser(any(UserRegisterDTO.class));

      mockMvc
          .perform(
              post("/register")
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                  .flashAttr("userRegisterDTO", validUserRegisterDTO))
          .andExpect(status().isOk())
          .andExpect(view().name("filmo/register"))
          .andExpect(model().hasErrors())
          .andExpect(model().attributeHasFieldErrors("userRegisterDTO", "username"));
    }

    @Test
    @DisplayName("Falla el registro cuando el formato de la fecha de nacimiento es incorrecto")
    void givenInvalidDateOfBirth_whenRegisterUser_thenShowDateOfBirthFieldError() throws Exception {
      mockMvc
          .perform(
              post("/register")
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                  .param("username", validUserRegisterDTO.username())
                  .param("email", validUserRegisterDTO.email())
                  .param("password", validUserRegisterDTO.password())
                  .param("passwordConfirm", validUserRegisterDTO.passwordConfirm())
                  .param("name", validUserRegisterDTO.name())
                  .param("surname", validUserRegisterDTO.surname())
                  .param("dateOfBirth", "1995-03-01"))
          .andExpect(status().isOk())
          .andExpect(view().name("filmo/register"))
          .andExpect(model().hasErrors())
          .andExpect(model().attributeHasFieldErrors("userRegisterDTO", "dateOfBirth"));
    }
  }
}
