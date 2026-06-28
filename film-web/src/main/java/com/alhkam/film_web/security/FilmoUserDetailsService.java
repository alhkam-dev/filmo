package com.alhkam.film_web.security;

import com.alhkam.film_web.dto.UserDTO;
import com.alhkam.film_web.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmoUserDetailsService implements UserDetailsService {

  @Autowired private final UserService userService;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String loginInput) throws UsernameNotFoundException {
    Pair<UserDTO, String> userAndPassword =
        userService
            .findUserAndPasswordByUsernameOrEmail(loginInput)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User with username or email: %s not found".formatted(loginInput)));

    return toUserDetails(userAndPassword.getLeft(), userAndPassword.getRight());
  }

  private UserDetails toUserDetails(UserDTO userDTO, String password) {
    List<SimpleGrantedAuthority> authorities =
        userDTO.getRoles().stream().map(rol -> new SimpleGrantedAuthority("ROLE_" + rol)).toList();

    return new CustomUserDetails(userDTO, password, authorities);
  }

  // Clase interna para tener en cuenta tanto el email como el username en la sesión
  @Getter
  public static class CustomUserDetails extends User {

    private final Long id;
    private final String email;

    public CustomUserDetails(
        UserDTO userDTO, String password, Collection<? extends GrantedAuthority> authorities) {
      super(userDTO.getUsername(), password, authorities);
      this.id = userDTO.getId();
      this.email = userDTO.getEmail();
    }
  }
}
