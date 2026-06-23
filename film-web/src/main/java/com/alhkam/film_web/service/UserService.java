package com.alhkam.film_web.service;

import com.alhkam.film_web.dto.UserDTO;
import com.alhkam.film_web.dto.UserRegisterDTO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public interface UserService {

  UserDTO findById(Long id);

  Optional<Pair<UserDTO, String>> findUserAndPasswordByUsernameOrEmail(String usernameOrEmail);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean registerUser(UserRegisterDTO userRegisterDTO);
}
