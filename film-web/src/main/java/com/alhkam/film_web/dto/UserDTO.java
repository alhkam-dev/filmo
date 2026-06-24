package com.alhkam.film_web.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private LocalDateTime created;
    private Set<String> roles;
}
