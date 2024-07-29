package com.vicheak.coreapp.api.auth;

import com.vicheak.coreapp.api.auth.web.*;
import com.vicheak.coreapp.api.user.User;
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

    /**
     * This method is used when the client forgets the password to the system
     * @param forgetPasswordDto is the request from client
     * @throws MessagingException
     */
    void forgetPassword(ForgetPasswordDto forgetPasswordDto) throws MessagingException;

    /**
     * This method is used to send verification code to client's email
     * @param email is the request from client
     * @return User
     * @throws MessagingException
     */
    User sendVerificationCode(String email) throws MessagingException;

    /**
     * This method is used to reset client's password after verify the account
     * @param resetPasswordDto is the request from client
     */
    void resetPassword(ResetPasswordDto resetPasswordDto);

}
