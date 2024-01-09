package com.vicheak.coreapp.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.vicheak.coreapp.util.KeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final KeyUtil keyUtil;

    @Bean
    public AuthenticationProvider authenticationProviderConfig() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() throws JOSEException {
        return new JwtAuthenticationProvider(jwtRefreshTokenDecoder());
    }

    @Bean
    public SecurityFilterChain securityFilterChainConfig(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> {
            //permitted endpoint without security
            auth.requestMatchers("/file/**").permitAll();
            //auth security
            auth.requestMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/refreshToken",
                    "/api/v1/auth/register",
                    "/api/v1/auth/verify").permitAll();

            //category security
            auth.requestMatchers(HttpMethod.GET, "/api/v1/categories/**").hasAuthority("SCOPE_category:read");
            auth.requestMatchers(HttpMethod.POST, "/api/v1/categories/**").hasAuthority("SCOPE_category:write");
            auth.requestMatchers(HttpMethod.PATCH, "/api/v1/categories/**").hasAuthority("SCOPE_category:update");
            auth.requestMatchers(HttpMethod.DELETE, "/api/v1/categories/**").hasAuthority("SCOPE_category:delete");

            //course security
            auth.requestMatchers(HttpMethod.GET, "/api/v1/courses/me").hasAuthority("SCOPE_ROLE_AUTHOR");
            auth.requestMatchers(HttpMethod.GET, "/api/v1/courses/**").hasAuthority("SCOPE_course:read");
            auth.requestMatchers(HttpMethod.POST, "/api/v1/courses/like").authenticated();
            auth.requestMatchers(HttpMethod.POST, "/api/v1/courses/**").hasAuthority("SCOPE_course:write");
            auth.requestMatchers(HttpMethod.PUT, "/api/v1/courses/**").hasAuthority("SCOPE_course:update");
            auth.requestMatchers(HttpMethod.PATCH, "/api/v1/courses/**").hasAuthority("SCOPE_course:update");
            auth.requestMatchers(HttpMethod.DELETE, "/api/v1/courses/**").hasAuthority("SCOPE_course:delete");

            //video security
            auth.requestMatchers(HttpMethod.GET, "/api/v1/videos/me").hasAuthority("SCOPE_ROLE_AUTHOR");
            auth.requestMatchers(HttpMethod.GET, "/api/v1/videos/**").hasAuthority("SCOPE_video:read");
            auth.requestMatchers(HttpMethod.POST, "/api/v1/videos/**").hasAuthority("SCOPE_video:write");
            auth.requestMatchers(HttpMethod.PUT, "/api/v1/videos/**").hasAuthority("SCOPE_video:update");
            auth.requestMatchers(HttpMethod.PATCH, "/api/v1/videos/**").hasAuthority("SCOPE_video:update");
            auth.requestMatchers(HttpMethod.DELETE, "/api/v1/videos/**").hasAuthority("SCOPE_video:delete");

            //file security
            auth.requestMatchers(HttpMethod.POST, "/api/v1/files/**").hasAuthority("SCOPE_file:write");

            //user security
            auth.requestMatchers(HttpMethod.GET, "/api/v1/users/me").hasAuthority("SCOPE_user:profile");
            auth.requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasAuthority("SCOPE_user:read");
            auth.requestMatchers(HttpMethod.POST, "/api/v1/users/**").hasAuthority("SCOPE_user:write");
            auth.requestMatchers(HttpMethod.PUT, "/api/v1/users/uploadImage/**").hasAuthority("SCOPE_user:update");
            auth.requestMatchers(HttpMethod.PUT, "/api/v1/users/**").hasAuthority("SCOPE_ROLE_ADMIN");
            auth.requestMatchers(HttpMethod.PATCH, "/api/v1/users/**").hasAuthority("SCOPE_user:update");
            auth.requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasAuthority("SCOPE_user:delete");

            //subscription security
            auth.requestMatchers(HttpMethod.GET, "/api/v1/subscriptions/author/reports").hasAuthority("SCOPE_ROLE_AUTHOR");
            auth.requestMatchers(HttpMethod.GET, "/api/v1/subscriptions/**").hasAuthority("SCOPE_subscription:read");
            auth.requestMatchers(HttpMethod.POST, "/api/v1/subscriptions/**").hasAuthority("SCOPE_subscription:write");
            auth.requestMatchers(HttpMethod.PUT, "/api/v1/subscriptions/**").hasAuthority("SCOPE_subscription:update");
            auth.requestMatchers(HttpMethod.DELETE, "/api/v1/subscriptions/**").hasAuthority("SCOPE_ROLE_AUTHOR");

            auth.anyRequest().authenticated();
        });

        http.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(Customizer.withDefaults()));

        http.csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    //access token beans
    @Bean
    @Primary
    public JWKSource<SecurityContext> jwkSource() {
        JWK jwk = new RSAKey.Builder(keyUtil.getAccessTokenPublicKey())
                .privateKey(keyUtil.getAccessTokenPrivateKey())
                .keyID(UUID.randomUUID().toString())
                .build();

        var jwkSet = new JWKSet(jwk);
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(keyUtil.getAccessTokenPublicKey()).build();
    }

    @Bean
    @Primary
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    //refresh token beans
    @Bean("refreshTokenJwkSource")
    public JWKSource<SecurityContext> refreshTokenJwkSource() {
        JWK jwk = new RSAKey.Builder(keyUtil.getRefreshTokenPublicKey())
                .privateKey(keyUtil.getRefreshTokenPrivateKey())
                .keyID(UUID.randomUUID().toString())
                .build();

        var jwkSet = new JWKSet(jwk);
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    @Bean("jwtRefreshTokenDecoder")
    public JwtDecoder jwtRefreshTokenDecoder() throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(keyUtil.getRefreshTokenPublicKey()).build();
    }

    @Bean("jwtRefreshTokenEncoder")
    public JwtEncoder jwtRefreshTokenEncoder(@Qualifier("refreshTokenJwkSource") JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

}
