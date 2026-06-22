package com.alhkam.film_web.repository.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.alhkam.film_web.domain.user.Role;
import com.alhkam.film_web.domain.user.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.support.TransactionTemplate;

@DataJpaTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;",
      "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=create-drop"
    })
class UserRespositoryTest {

  @Autowired private RoleRepository roleRepository;
  @Autowired private UserRespository userRespository;
  @Autowired private TransactionTemplate transactionTemplate;

  @BeforeEach
  void beforeEach() {
    Role userRole = Role.builder().name("USER").build();
    Role adminRole = Role.builder().name("ADMIN").build();

    List<Role> savedRoles = roleRepository.saveAll(List.of(userRole, adminRole));
    Role savedUser = savedRoles.get(0);
    Role savedAdmin = savedRoles.get(1);

    User user1 =
        User.builder()
            .username("filmo")
            .password("password1")
            .email("filmo@filmo.com")
            .roles(new HashSet<>(Set.of(savedUser, savedAdmin)))
            .name("name1")
            .surname("surname1")
            .dateOfBirth(LocalDate.of(1995, 3, 1))
            .build();

    User user2 =
        User.builder()
            .username("user")
            .password("password2")
            .email("user@filmo.com")
            .roles(new HashSet<>(Set.of(savedUser)))
            .name("name2")
            .surname("surname2")
            .dateOfBirth(LocalDate.of(1995, 3, 1))
            .build();

    userRespository.saveAll(List.of(user1, user2));
  }

  @Test
  void givenTwoUsers_whenFindByUsernameWithRoles_thenReturnCorrectUser() {
    transactionTemplate.executeWithoutResult(
        transactionStatus -> {
          Optional<User> usersByUsername = userRespository.findByUsernameOrEmailWithRoles("filmo");

          assertThat(usersByUsername).isPresent();

          assertThat(usersByUsername.get())
              .returns("filmo", User::getUsername)
              .returns("filmo@filmo.com", User::getEmail)
              .satisfies(user -> assertThat(user.getCreated()).isNotNull())
              .satisfies(user -> assertThat(user.getId()).isNotNull())
              .satisfies(
                  user ->
                      assertThat(user.getRoles().stream().map(Role::getName).toList())
                          .containsExactlyInAnyOrder("USER", "ADMIN"));
        });
  }

  @Test
  void givenTwoUsers_whenFindByEmailWithRoles_thenReturnOk() {
    transactionTemplate.executeWithoutResult(
        transactionStatus -> {
          Optional<User> usersByUsername =
              userRespository.findByUsernameOrEmailWithRoles("user@filmo.com");

          assertThat(usersByUsername).isPresent();

          assertThat(usersByUsername.get())
              .returns("user", User::getUsername)
              .returns("user@filmo.com", User::getEmail)
              .satisfies(user -> assertThat(user.getCreated()).isNotNull())
              .satisfies(user -> assertThat(user.getId()).isNotNull())
              .satisfies(
                  user ->
                      assertThat(user.getRoles().stream().map(Role::getName).toList())
                          .containsExactlyInAnyOrder("USER"));
        });
  }
}
