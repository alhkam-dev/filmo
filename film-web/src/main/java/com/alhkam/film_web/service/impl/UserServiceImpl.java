package com.alhkam.film_web.service.impl;

import com.alhkam.film_web.domain.Role;
import com.alhkam.film_web.domain.User;
import com.alhkam.film_web.dto.UserDTO;
import com.alhkam.film_web.dto.UserRegisterDTO;
import com.alhkam.film_web.exception.EmailAlreadyExistsException;
import com.alhkam.film_web.exception.UsernameAlreadyExistsException;
import com.alhkam.film_web.repository.RoleRepository;
import com.alhkam.film_web.repository.UserRespository;
import com.alhkam.film_web.service.SecurityService;
import com.alhkam.film_web.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRespository userRespository;
  private final RoleRepository roleRepository;
  private final SecurityService securityService;

  @Override
  @Transactional(readOnly = true)
  public UserDTO findById(Long id) {
    return userRespository
        .findById(id)
        .map(this::convertToDTO)
        .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + id));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserDTO> findByUsernameOrEmailWithRoles(String usernameOrEmail) {
    return userRespository.findByUsernameOrEmailWithRoles(usernameOrEmail).map(this::convertToDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByUsername(String username) {
    return userRespository.existsByUsername(username);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByEmail(String email) {
    return userRespository.existsByEmail(email);
  }

  @Override
  @Transactional
  public boolean registerUser(UserRegisterDTO userRegisterDTO) {

    if (userRegisterDTO == null) {
      throw new IllegalArgumentException("User registration data cannot be null");
    }

    if (userRegisterDTO.username() == null
        || userRegisterDTO.username().isBlank()
        || userRegisterDTO.email() == null
        || userRegisterDTO.email().isBlank()) {
      throw new IllegalArgumentException("Username and Email are strictly required");
    }

    if (userRespository.existsByUsername(userRegisterDTO.username())) {
      throw new UsernameAlreadyExistsException("Username is already in use");
    }

    if (userRespository.existsByEmail(userRegisterDTO.email())) {
      throw new EmailAlreadyExistsException("Email is already in use");
    }

    User userToRegister = new User();
    userToRegister.setUsername(userRegisterDTO.username().trim());
    userToRegister.setEmail(userRegisterDTO.email().trim().toLowerCase());
    userToRegister.setName(userRegisterDTO.name().trim());
    userToRegister.setSurname(userRegisterDTO.surname().trim());
    userToRegister.setDateOfBirth(userRegisterDTO.dateOfBirth());

    userToRegister.setPassword(securityService.encodePassword(userRegisterDTO.password()));

    Role userRole =
        roleRepository
            .findByName("USER")
            .orElseThrow(
                () -> new IllegalStateException("Role 'USER' could not be found in the database"));
    userToRegister.getRoles().add(userRole);

    userRespository.save(userToRegister);
    return true;
  }

  private UserDTO convertToDTO(User user) {
    return UserDTO.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .name(user.getName())
        .surname(user.getSurname())
        .dateOfBirth(user.getDateOfBirth())
        .created(user.getCreated())
        .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
        .build();
  }
}
