package com.vicheak.coreapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationProvider authenticationProviderConfig() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChainConfig(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> {
            //permitted endpoint without security
            auth.requestMatchers("/file/**").permitAll();
            //auth security
            auth.requestMatchers("/api/v1/auth/register", "/api/v1/auth/verify").permitAll();

            //category security
            auth.requestMatchers(HttpMethod.GET, "/api/v1/categories/**").hasAuthority("category:read");
            auth.requestMatchers(HttpMethod.POST, "/api/v1/categories/**").hasAuthority("category:write");
            auth.requestMatchers(HttpMethod.PATCH, "/api/v1/categories/**").hasAuthority("category:update");
            auth.requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**").hasAuthority("category:delete");

            //course security
            auth.requestMatchers(HttpMethod.GET, "/api/v1/courses/me").hasAuthority("ROLE_AUTHOR");
            auth.requestMatchers(HttpMethod.GET, "/api/v1/courses/**").hasAuthority("course:read");
            auth.requestMatchers(HttpMethod.POST, "/api/v1/courses/like").authenticated();
            auth.requestMatchers(HttpMethod.POST, "/api/v1/courses/**").hasAuthority("course:write");
            auth.requestMatchers(HttpMethod.PUT, "/api/v1/courses/**").hasAuthority("course:update");
            auth.requestMatchers(HttpMethod.PATCH, "/api/v1/courses/**").hasAuthority("course:update");
            auth.requestMatchers(HttpMethod.DELETE, "/api/v1/courses/**").hasAuthority("course:delete");

            //video security
            auth.requestMatchers(HttpMethod.GET, "/api/v1/videos/me").hasAuthority("ROLE_AUTHOR");
            auth.requestMatchers(HttpMethod.GET, "/api/v1/videos/**").hasAuthority("video:read");
            auth.requestMatchers(HttpMethod.POST, "/api/v1/videos/**").hasAuthority("video:write");
            auth.requestMatchers(HttpMethod.PUT, "/api/v1/videos/**").hasAuthority("video:update");
            auth.requestMatchers(HttpMethod.PATCH, "/api/v1/videos/**").hasAuthority("video:update");
            auth.requestMatchers(HttpMethod.DELETE, "/api/v1/videos/**").hasAuthority("video:delete");

            //file security
            auth.requestMatchers(HttpMethod.POST, "/api/v1/files/**").hasAuthority("file:write");

            //user security
            auth.requestMatchers(HttpMethod.GET, "/api/v1/users/me").hasAuthority("user:profile");
            auth.requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAuthority("user:read");
            auth.requestMatchers(HttpMethod.POST, "/api/v1/users/**").hasAuthority("user:write");
            auth.requestMatchers(HttpMethod.PUT, "/api/v1/users/uploadImage/**").hasAuthority("user:update");
            auth.requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasAuthority("ROLE_ADMIN");
            auth.requestMatchers(HttpMethod.PATCH, "/api/v1/users/**").hasAuthority("user:update");
            auth.requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasAuthority("user:delete");

            //subscription security
            auth.requestMatchers(HttpMethod.GET, "/api/v1/subscriptions/author/reports").hasAuthority("ROLE_AUTHOR");
            auth.requestMatchers(HttpMethod.GET, "/api/v1/subscriptions/**").hasAuthority("subscription:read");
            auth.requestMatchers(HttpMethod.POST, "/api/v1/subscriptions/**").hasAuthority("subscription:write");
            auth.requestMatchers(HttpMethod.PUT, "/api/v1/subscriptions/**").hasAuthority("subscription:update");
            auth.requestMatchers(HttpMethod.DELETE, "/api/v1/subscriptions/**").hasAuthority("ROLE_AUTHOR");

            auth.anyRequest().authenticated();
        });

        http.httpBasic(Customizer.withDefaults());

        http.csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
