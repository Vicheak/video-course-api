package com.vicheak.coreapp.security;

import com.vicheak.coreapp.api.user.User;
import com.vicheak.coreapp.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityContextHelper {

    private final UserRepository userRepository;

    public User loadAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        //load authenticated user object
        return userRepository.findByEmail(jwt.getId())
                .orElseThrow(
                        () -> new UsernameNotFoundException("Email has not been authenticated!")
                );
    }

}
