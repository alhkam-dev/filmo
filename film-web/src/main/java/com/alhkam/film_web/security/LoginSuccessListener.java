package com.alhkam.film_web.security;

import com.alhkam.film_web.repository.UserRespository;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class LoginSuccessListener {

    private final UserRespository userRespository;

    @EventListener
    @Transactional
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();

        if(principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();

            userRespository.findByUsername(username).ifPresent(user -> {
                user.setLastLogin(LocalDateTime.now());
            });
        }
    }
}
