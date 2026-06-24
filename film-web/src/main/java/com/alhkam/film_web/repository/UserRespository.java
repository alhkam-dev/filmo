package com.alhkam.film_web.repository;

import com.alhkam.film_web.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRespository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  @Query(
      "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
  Optional<User> findByUsernameOrEmailWithRoles(String usernameOrEmail);
}
