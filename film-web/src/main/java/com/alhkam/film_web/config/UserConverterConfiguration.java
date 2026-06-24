package com.alhkam.film_web.config;

import com.alhkam.film_web.domain.Role;
import com.alhkam.film_web.domain.User;
import com.alhkam.film_web.dto.UserDTO;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class UserConverterConfiguration {

  public UserConverterConfiguration(ModelMapper modelMapper) {

    // Convertidor
    Converter<Set<Role>, Set<String>> roleConverter =
        context -> {
          Set<Role> source = context.getSource();
          if (source == null) return Set.of();
          return source.stream().map(Role::getName).collect(Collectors.toSet());
        };

    // Mapeo
    modelMapper
        .typeMap(User.class, UserDTO.class)
        .addMappings(
            mapper ->
                mapper
                    .using(roleConverter)
                    .map(User::getRoles, UserDTO::setRoles));
  }
}
