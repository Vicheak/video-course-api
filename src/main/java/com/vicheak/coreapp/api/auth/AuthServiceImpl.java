package com.vicheak.coreapp.api.auth;

import com.vicheak.coreapp.api.auth.web.*;
import com.vicheak.coreapp.api.authority.Role;
import com.vicheak.coreapp.api.mail.Mail;
import com.vicheak.coreapp.api.mail.MailService;
import com.vicheak.coreapp.api.user.*;
import com.vicheak.coreapp.api.user.web.TransactionUserDto;
import com.vicheak.coreapp.security.CustomUserDetails;
import com.vicheak.coreapp.security.SecurityContextHelper;
import com.vicheak.coreapp.util.RandomUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final AuthMapper authMapper;
    private final UserServiceImpl userService;
    private final UserRoleRepository userRoleRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextHelper securityContextHelper;
    private final JwtEncoder jwtEncoder;
    private AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private JwtEncoder jwtRefreshTokenEncoder;

    @Autowired
    public void setAuthenticationProvider(@Qualifier("authenticationProviderConfig") AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Autowired
    public void setJwtRefreshTokenEncoder(@Qualifier("jwtRefreshTokenEncoder") JwtEncoder jwtRefreshTokenEncoder) {
        this.jwtRefreshTokenEncoder = jwtRefreshTokenEncoder;
    }

    @Value("${spring.mail.username}")
    private String adminMail;

    @Override
    public AuthDto login(LoginDto loginDto) {
        //authenticate with email and password
        Authentication auth = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
        //use bean authentication provider to authenticate with user details service
        auth = authenticationProvider.authenticate(auth);

        //claim payload must be joined with space and default authority pattern is SCOPE_
        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        return AuthDto.builder()
                .type("Bearer")
                .accessToken(generateAccessToken(GenerateTokenDto.builder()
                        .auth(auth.getName())
                        .scope(scope)
                        .expiration(Instant.now().plus(1, ChronoUnit.HOURS))
                        .build()))
                .refreshToken(generateRefreshToken(GenerateTokenDto.builder()
                        .auth(auth.getName())
                        .scope(scope)
                        .expiration(Instant.now().plus(30, ChronoUnit.DAYS))
                        .build()))
                .build();
    }

    @Override
    public AuthDto refreshToken(RefreshTokenDto refreshTokenDto) {
        //authenticate the refresh token using bearer token authentication
        Authentication auth = new BearerTokenAuthenticationToken(refreshTokenDto.refreshToken());
        auth = jwtAuthenticationProvider.authenticate(auth);

        Jwt jwt = (Jwt) auth.getPrincipal();

        return AuthDto.builder()
                .type("Bearer")
                .accessToken(generateAccessToken(GenerateTokenDto.builder()
                        .auth(jwt.getId())
                        .scope(jwt.getClaimAsString("scope"))
                        .expiration(Instant.now().plus(1, ChronoUnit.HOURS))
                        .build()))
                .refreshToken(generateRefreshTokenCheckDuration(GenerateTokenDto.builder()
                        .auth(jwt.getId())
                        .scope(jwt.getClaimAsString("scope"))
                        .previousToken(refreshTokenDto.refreshToken())
                        .expiration(Instant.now().plus(30, ChronoUnit.DAYS))
                        .duration(Duration.between(Instant.now(), jwt.getExpiresAt()))
                        .checkDurationNumber(7)
                        .build()))
                .build();
    }

    private String generateAccessToken(GenerateTokenDto generateTokenDto) {
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .id(generateTokenDto.auth())
                .issuer("public")
                .issuedAt(Instant.now())
                .expiresAt(generateTokenDto.expiration())
                .subject("Access Token")
                .audience(List.of("Public Client"))
                .claim("scope", generateTokenDto.scope())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    private String generateRefreshToken(GenerateTokenDto generateTokenDto) {
        JwtClaimsSet jwtRefreshTokenClaimsSet = JwtClaimsSet.builder()
                .id(generateTokenDto.auth())
                .issuer("public")
                .issuedAt(Instant.now())
                .expiresAt(generateTokenDto.expiration())
                .subject("Refresh Token")
                .audience(List.of("Public Client"))
                .claim("scope", generateTokenDto.scope())
                .build();
        return jwtRefreshTokenEncoder.encode(JwtEncoderParameters.from(jwtRefreshTokenClaimsSet)).getTokenValue();
    }

    private String generateRefreshTokenCheckDuration(GenerateTokenDto generateTokenDto) {
        if (generateTokenDto.duration().toDays() < generateTokenDto.checkDurationNumber()) {
            JwtClaimsSet jwtRefreshTokenClaimsSet = JwtClaimsSet.builder()
                    .id(generateTokenDto.auth())
                    .issuer("public")
                    .issuedAt(Instant.now())
                    .expiresAt(generateTokenDto.expiration())
                    .subject("Refresh Token")
                    .audience(List.of("Public Client"))
                    .claim("scope", generateTokenDto.scope())
                    .build();
            return jwtRefreshTokenEncoder.encode(JwtEncoderParameters.from(jwtRefreshTokenClaimsSet)).getTokenValue();
        }
        return generateTokenDto.previousToken();
    }

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
        User authenticatedUser = securityContextHelper.loadAuthenticatedUser();
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
