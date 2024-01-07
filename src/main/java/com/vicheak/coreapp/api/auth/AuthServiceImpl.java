package com.vicheak.coreapp.api.auth;

import com.vicheak.coreapp.api.auth.web.ApplyAuthorDto;
import com.vicheak.coreapp.api.auth.web.RegisterDto;
import com.vicheak.coreapp.api.auth.web.VerifyDto;
import com.vicheak.coreapp.api.authority.Role;
import com.vicheak.coreapp.api.mail.Mail;
import com.vicheak.coreapp.api.mail.MailService;
import com.vicheak.coreapp.api.user.*;
import com.vicheak.coreapp.api.user.web.TransactionUserDto;
import com.vicheak.coreapp.security.CustomUserDetails;
import com.vicheak.coreapp.util.RandomUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final AuthMapper authMapper;
    private final UserServiceImpl userService;
    private final UserRoleRepository userRoleRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String adminMail;

    @Transactional
    @Override
    public void register(RegisterDto registerDto) throws MessagingException {
        //map from registerDto to transactionUserDto
        TransactionUserDto transactionUserDto =
                authMapper.fromRegisterDtoToTransactionUserDto(registerDto);

        //validate the dto
        userService.validateTransactionUserDto(transactionUserDto);

        //map from dto to entity
        User newUser = userService.setupNewUser(transactionUserDto);

        //set up the subscriber role
        List<UserRole> userRoles = List.of(
                UserRole.builder()
                        .user(newUser)
                        .role(Role.builder().id(3).build())
                        .build()
        );

        userRoleRepository.saveAll(userRoles);

        //set up user roles
        newUser.setUserRoles(userRoles);

        authRepository.save(newUser);

        updateVerifiedCodeAndSendMail(newUser, "Java School Email Verification");
    }

    @Transactional
    @Override
    public void verify(VerifyDto verifyDto) {
        //load the unverified user by email and verified code
        User verifiedUser = authRepository.findByEmailAndVerifiedCodeAndVerifiedFalseAndEnabledFalse(
                        verifyDto.email(), verifyDto.verifiedCode())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                "Email verification has been failed!")
                );

        //make the user verified
        verifiedUser.setVerified(true);
        verifiedUser.setEnabled(true);
        verifiedUser.setVerifiedCode(null);

        authRepository.save(verifiedUser);
    }

    @Transactional
    @Override
    public void applyAuthor(ApplyAuthorDto applyAuthorDto) throws MessagingException {
        //check security context holder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        User authenticatedUser = customUserDetails.getUser();
        checkIfUserIsAuthor(authenticatedUser);

        //load the credentials
        User verifiedUser = authRepository.findByEmailAndPhoneNumberAndVerifiedTrueAndEnabledTrue(
                        applyAuthorDto.email(), applyAuthorDto.phoneNumber())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                "Author application has been failed!")
                );

        //check encrypted raw password
        if (!passwordEncoder.matches(applyAuthorDto.password(), verifiedUser.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Author application has been failed!");

        updateVerifiedCodeAndSendMail(verifiedUser, "Java School Author Application");
    }

    @Transactional
    @Override
    public void verifyAuthor(VerifyDto verifyDto) {
        //load the credentials
        User verifiedUser = authRepository.findByEmailAndVerifiedCodeAndVerifiedTrueAndEnabledTrue(
                        verifyDto.email(), verifyDto.verifiedCode())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                                "Author application verification has been failed!")
                );

        //set up author role for the verified author
        verifiedUser.setVerifiedCode(null);

        List<UserRole> userRoles = new ArrayList<>();

        userRoles.add(UserRole.builder()
                .user(verifiedUser)
                .role(Role.builder().id(2).build())
                .build());

        userRoleRepository.saveAll(userRoles);

        authRepository.save(verifiedUser);
    }

    private void updateVerifiedCodeAndSendMail(User user, String subject) throws MessagingException {
        //generate six random digit for verification code
        String verifiedCode = RandomUtil.getRandomNumber();

        //update verification code
        authRepository.updateVerifiedCode(user.getUsername(), verifiedCode);

        Mail<String> verifiedMail = new Mail<>();
        verifiedMail.setSender(adminMail);
        verifiedMail.setReceiver(user.getEmail());
        verifiedMail.setSubject(subject);
        verifiedMail.setTemplate("auth/verify-mail.html");
        verifiedMail.setMetaData(verifiedCode);

        mailService.sendMail(verifiedMail);
    }

    private void checkIfUserIsAuthor(User user) {
        List<UserRole> userRoles = user.getUserRoles();
        userRoles.forEach(userRole -> {
            if (userRole.getRole().getName().equals("AUTHOR"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "You are already an author in our system!");
        });
    }

}
