package com.alhkam.film_web.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.alhkam.film_web.domain.Role;
import com.alhkam.film_web.domain.User;
import com.alhkam.film_web.dto.UserDTO;
import com.alhkam.film_web.dto.UserRegisterDTO;
import com.alhkam.film_web.exception.EmailAlreadyExistsException;
import com.alhkam.film_web.exception.UsernameAlreadyExistsException;
import com.alhkam.film_web.repository.RoleRepository;
import com.alhkam.film_web.repository.UserRespository;
import com.alhkam.film_web.service.SecurityService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock private UserRespository userRespository;

  @Mock private RoleRepository roleRepository;

  @Mock private SecurityService securityService;

  @Mock private ModelMapper modelMapper;

  @InjectMocks private UserServiceImpl userService;

  private User sampleUser;
  private UserDTO sampleUserDTO;
  private Role defaultRole;

  @BeforeEach
  void setUp() {
    defaultRole = Role.builder().id(1L).name("USER").build();

    sampleUser =
        User.builder()
            .id(1L)
            .username("user")
            .email("user@filmo.com")
            .name("User")
            .surname("Filmo")
            .password("encrypted_password")
            .dateOfBirth(LocalDate.of(1995, 3, 1))
            .roles(new HashSet<>())
            .build();

    sampleUserDTO = UserDTO.builder().id(1L).username("user").roles(Set.of("USER")).build();
  }

  @Nested
  @DisplayName("Tests de búsqueda")
  class SearchTests {

    @Test
    @DisplayName("Retorna UserDTO cuando el ID existe")
    void givenIdExists_whenFindById_thenReturnUserDTO() {
      when(userRespository.findById(1L)).thenReturn(Optional.of(sampleUser));
      when(modelMapper.map(sampleUser, UserDTO.class)).thenReturn(sampleUserDTO);

      UserDTO result = userService.findById(1L);

      assertThat(result).isNotNull();
      assertThat(result.username()).isEqualTo("user");

      verify(userRespository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Lanza EntityNotFoundException cuando el Id no existe")
    void givenIdDoesNotExist_whenFindById_thenThrowException() {
      when(userRespository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.findById(99L))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("Usuario no encontrado con id: 99");
    }
  }

  @Nested
  @DisplayName("Tests para findUserAndPasswordByUsernameOrEmail")
  class FindUserAndPasswordByUsernameOrEmailTests {

    @Test
    @DisplayName("Devuelve el Pair cuando usuario existe por username o email")
    void givenUsernameOrEmail_whenUserExists_thenReturnPair() {
      String usernameOrEmail = "user";
      when(userRespository.findByUsernameOrEmailWithRoles(usernameOrEmail))
          .thenReturn(Optional.of(sampleUser));
      when(modelMapper.map(sampleUser, UserDTO.class)).thenReturn(sampleUserDTO);

      Optional<Pair<UserDTO, String>> result = userService.findUserAndPasswordByUsernameOrEmail(usernameOrEmail);

      assertTrue(result.isPresent());
      assertThat(result.get().getLeft()).isEqualTo(sampleUserDTO);
      assertThat(result.get().getRight()).isEqualTo("encrypted_password");

      verify(userRespository, times(1)).findByUsernameOrEmailWithRoles(usernameOrEmail);
    }

    @Test
    @DisplayName("Devuelve un Optional vacío cuando el usuario no existe")
    void shouldReturnEmpty_WhenUserDoesNotExist() {
      String inputLogin = "inexistente";
      when(userRespository.findByUsernameOrEmailWithRoles(inputLogin)).thenReturn(Optional.empty());

      Optional<Pair<UserDTO, String>> result = userService.findUserAndPasswordByUsernameOrEmail(inputLogin);

      assertTrue(result.isEmpty());
      verifyNoInteractions(modelMapper);
    }
  }

  @Nested
  @DisplayName("Tests de registro de usuario")
  class RegisterUserTests {
    private UserRegisterDTO userRegisterDTO;

    @BeforeEach
    void setUpRegister() {
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

    @Test
    @DisplayName("Registro con éxito cuando datos válidos y únicos")
    void givenValidUserRegisterDTO_whenRegisterUser_thenReturnTrue() {
      when(userRespository.existsByUsername(userRegisterDTO.username())).thenReturn(false);
      when(userRespository.existsByEmail(userRegisterDTO.email())).thenReturn(false);
      when(securityService.encodePassword(userRegisterDTO.password()))
          .thenReturn("encrypted_password");
      when(roleRepository.findByName("USER")).thenReturn(Optional.of(defaultRole));
      when(userRespository.save(any(User.class))).thenReturn(sampleUser);

      boolean isUserRegistered = userService.registerUser(userRegisterDTO);

      assertThat(isUserRegistered).isTrue();
      verify(userRespository, times(1)).save(any(User.class));
      verify(securityService, times(1)).encodePassword("password");
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el DTO es nulo")
    void givenNullUserRegisterDTO_whenRegisterUser_thenThrowException() {
      assertThatThrownBy(() -> userService.registerUser(null))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("User registration data cannot be null");
    }

    @Test
    @DisplayName(
        "Debe lanzar UsernameAlreadyExistsException si el nombre de usuario ya está en uso")
    void givenUserRegisterDTOWithUsernameInUse_whenRegisterUser_thenThrowException() {
      when(userRespository.existsByUsername(userRegisterDTO.username())).thenReturn(true);

      assertThatThrownBy(() -> userService.registerUser(userRegisterDTO))
          .isInstanceOf(UsernameAlreadyExistsException.class)
          .hasMessageContaining("Username is already in use");
    }

    @Test
    @DisplayName("Debe lanzar EmailAlreadyExistsException si el email ya está en uso")
    void givenUserRegisterDTOWithEmailInUse_whenRegisterUser_thenThrowException() {
      when(userRespository.existsByUsername(userRegisterDTO.username())).thenReturn(false);
      when(userRespository.existsByEmail(userRegisterDTO.email())).thenReturn(true);

      assertThatThrownBy(() -> userService.registerUser(userRegisterDTO))
          .isInstanceOf(EmailAlreadyExistsException.class)
          .hasMessageContaining("Email is already in use");
    }

    @Test
    @DisplayName("Debe lanzar IllegalStateException si el rol 'USER' no existe en base de datos")
    void givenUserRegisterDTO_whenRegisterUserAndRoleNotFound_thenThrowException() {
      when(userRespository.existsByUsername(userRegisterDTO.username())).thenReturn(false);
      when(userRespository.existsByEmail(userRegisterDTO.email())).thenReturn(false);
      when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.registerUser(userRegisterDTO))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Role 'USER' could not be found in the database");
    }
  }
}
