package com.alhkam.film_web.controller;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.alhkam.film_web.dto.UserRegisterDTO;
import com.alhkam.film_web.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Objects;

@WebMvcTest(RegisterController.class)
@DisplayName("Tests para RegisterController")
class RegisterControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;

  private UserRegisterDTO userRegisterDTO;

  @BeforeEach
  void setUp() {
    userRegisterDTO =
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
              .andExpect(view().name("film/register"))
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
}
