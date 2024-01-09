package com.vicheak.coreapp.api.auth.web;

import com.vicheak.coreapp.api.auth.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/login")
    public AuthDto login(@RequestBody @Valid LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/refreshToken")
    public AuthDto refreshToken(@RequestBody @Valid RefreshTokenDto refreshTokenDto) {
        return authService.refreshToken(refreshTokenDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody @Valid RegisterDto registerDto) throws MessagingException {
        authService.register(registerDto);
        return Map.of("message", "Please check your email for verification code!");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verify")
    public Map<String, String> verify(@RequestBody @Valid VerifyDto verifyDto) {
        authService.verify(verifyDto);
        return Map.of("message", "Congratulation! Your email has been verified...!");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/applyForAuthor")
    public Map<String, String> applyAuthor(@RequestBody @Valid ApplyAuthorDto applyAuthorDto) throws MessagingException {
        authService.applyAuthor(applyAuthorDto);
        return Map.of("message", "Please check your email for verification code!");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verifyAuthor")
    public Map<String, String> verifyAuthor(@RequestBody @Valid VerifyDto verifyDto) {
        authService.verifyAuthor(verifyDto);
        return Map.of("message", "Congratulation! Your application for author has been approved...!");
    }

}
