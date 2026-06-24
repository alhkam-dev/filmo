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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRespository userRespository;
  private final RoleRepository roleRepository;
  private final SecurityService securityService;

  private final ModelMapper modelMapper;

  @Override
  @Transactional(readOnly = true)
  public UserDTO findById(Long id) {
    return userRespository
        .findById(id)
        .map(user -> modelMapper.map(user, UserDTO.class))
        .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
  }

  @Override
  public Optional<Pair<UserDTO, String>> findUserAndPasswordByUsernameOrEmail(
      String usernameOrEmail) {
    return userRespository
        .findByUsernameOrEmailWithRoles(usernameOrEmail)
        .map(user -> Pair.of(modelMapper.map(user, UserDTO.class), user.getPassword()));
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

    if (this.existsByUsername(userRegisterDTO.username())) {
      throw new UsernameAlreadyExistsException("Username is already in use");
    }

    if (this.existsByEmail(userRegisterDTO.email())) {
      throw new EmailAlreadyExistsException("Email is already in use");
    }

    User userToRegister = new User();
    userToRegister.setUsername(userRegisterDTO.username().trim());
    userToRegister.setEmail(userRegisterDTO.email().trim().toLowerCase());
    userToRegister.setName(userRegisterDTO.name().trim());
    userToRegister.setSurname(userRegisterDTO.surname().trim());

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate localDateParsed = LocalDate.parse(userRegisterDTO.dateOfBirth(), formatter);
    userToRegister.setDateOfBirth(localDateParsed);

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
}
