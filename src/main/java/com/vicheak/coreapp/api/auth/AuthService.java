package com.vicheak.coreapp.api.auth;

import com.vicheak.coreapp.api.auth.web.*;
import jakarta.mail.MessagingException;

public interface AuthService {

    /**
     * This method is used to authenticate a valid user
     * @param loginDto is the request from client
     * @return AuthDto
     */
    AuthDto login(LoginDto loginDto);

    /**
     * This method is used to generate new access token after expiration
     * @param refreshTokenDto is the request from client
     * @return AuthDto
     */
    AuthDto refreshToken(RefreshTokenDto refreshTokenDto);

    /**
     * This method is used to register a user for the role SUBSCRIBER
     * @param registerDto is the request from client
     */
    void register(RegisterDto registerDto) throws MessagingException;

    /**
     * This method is used to verify the email via verification code
     * @param verifyDto is the request from client
     */
    void verify(VerifyDto verifyDto);

    /**
     * This method is used to verify the email and apply for author
     * @param applyAuthorDto is the request from client
     */
    void applyAuthor(ApplyAuthorDto applyAuthorDto) throws MessagingException;

    /**
     * This method is used to verify author by email and verification code
     * @param verifyDto is the request from client
     */
    void verifyAuthor(VerifyDto verifyDto);

}
