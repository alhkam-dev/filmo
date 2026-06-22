package com.alhkam.film_web.service;

import com.alhkam.film_web.dto.UserDTO;
import com.alhkam.film_web.dto.UserRegisterDTO;

import java.util.Optional;

public interface UserService {

  UserDTO findById(Long id);

  Optional<UserDTO> findByUsernameOrEmailWithRoles(String usernameOrEmail);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean registerUser(UserRegisterDTO userRegisterDTO);
}
