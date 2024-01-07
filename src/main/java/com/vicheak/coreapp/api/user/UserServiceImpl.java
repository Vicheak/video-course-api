package com.vicheak.coreapp.api.user;

import com.vicheak.coreapp.api.authority.Role;
import com.vicheak.coreapp.api.authority.RoleRepository;
import com.vicheak.coreapp.api.file.FileService;
import com.vicheak.coreapp.api.file.web.FileDto;
import com.vicheak.coreapp.api.user.web.TransactionUserDto;
import com.vicheak.coreapp.api.user.web.UserDto;
import com.vicheak.coreapp.security.CustomUserDetails;
import com.vicheak.coreapp.util.FormatUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> loadAllUsers() {
        return userMapper.fromUserToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto loadUserByUuid(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "User with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        return userMapper.fromUserToUserDto(user);
    }

    @Transactional
    @Override
    public void createNewUser(TransactionUserDto transactionUserDto) {
        //call this method to validate dto
        if (Objects.isNull(transactionUserDto.roleIds()) || transactionUserDto.roleIds().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User must have at least one role!");

        validateTransactionUserDto(transactionUserDto);

        //check roles if exist
        boolean allExisted = transactionUserDto.roleIds().stream()
                .allMatch(roleRepository::existsById);

        if (!allExisted)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Role is not valid in the system! please check!");

        User user = setupNewUser(transactionUserDto);

        user.setVerified(true);
        user.setEnabled(true);

        List<UserRole> userRoles = new ArrayList<>();

        transactionUserDto.roleIds().forEach(roleId ->
                userRoles.add(UserRole.builder()
                        .user(user)
                        .role(Role.builder().id(roleId).build())
                        .build()));

        userRoleRepository.saveAll(userRoles);

        //set up user roles
        user.setUserRoles(userRoles);

        userRepository.save(user);
    }

    @Override
    public void updateUserByUuid(String uuid, TransactionUserDto transactionUserDto) {
        //load the user by uuid
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "User with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        //check security context holder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkSecurityOperation(user, auth);

        //all fields are validated here

        //check username if already exist
        if (Objects.nonNull(transactionUserDto.username()))
            if (!transactionUserDto.username().equalsIgnoreCase(user.getUsername()) &&
                    userRepository.existsByUsernameIgnoreCase(transactionUserDto.username()))
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Username conflicts resource in the system!");

        //check email if already exist
        if (Objects.nonNull(transactionUserDto.email()))
            if (!transactionUserDto.email().equalsIgnoreCase(user.getEmail()) &&
                    userRepository.existsByEmailIgnoreCase(transactionUserDto.email()))
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Email conflicts resource in the system!");

        //check phone number format and existence
        if (Objects.nonNull(transactionUserDto.phoneNumber())) {
            if (!FormatUtil.checkPhoneFormat(transactionUserDto.phoneNumber()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Phone Number is not in a valid format!");

            if (!transactionUserDto.phoneNumber().equals(user.getPhoneNumber()) &&
                    userRepository.existsByPhoneNumber(transactionUserDto.phoneNumber()))
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Phone Number conflicts resource in the system!");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        User authenticatedUser = customUserDetails.getUser();
        //check if user is an admin
        if (checkIfUserIsADMIN(authenticatedUser))
            //check roles if exist
            if (Objects.nonNull(transactionUserDto.roleIds()))
                if (transactionUserDto.roleIds().isEmpty())
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "User must have at least one role!");
                else {
                    boolean allExisted = transactionUserDto.roleIds().stream()
                            .allMatch(roleRepository::existsById);

                    if (!allExisted)
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Role is not valid in the system! please check!");

                    //process to remove previous user roles
                    userRoleRepository.deleteAll(user.getUserRoles());
                }

        //map from dto to entity but except the null value from dto
        userMapper.fromTransactionUserDtoToUser(user, transactionUserDto);

        //check if user is an admin
        if (checkIfUserIsADMIN(authenticatedUser))
            if (Objects.nonNull(transactionUserDto.roleIds()))
                if (!transactionUserDto.roleIds().isEmpty()) {
                    List<UserRole> userRoles = updateUserRolesTransaction(user, transactionUserDto.roleIds());

                    userRoleRepository.saveAll(userRoles);
                }

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUserByUuid(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "User with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        //check security context holder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkAdminRemoveOperation(user, auth);

        //remove from user roles
        userRoleRepository.deleteAll(user.getUserRoles());

        userRepository.delete(user);
    }

    @Transactional
    @Override
    public void updateUserIsEnabledByUuid(String uuid, Boolean isEnabled) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "User with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        user.setEnabled(isEnabled);

        userRepository.save(user);
    }

    @Transactional
    @Override
    public FileDto uploadUserPhotoByUuid(String uuid, MultipartFile file) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "User with uuid, %s has not been found in the system!"
                                        .formatted(uuid))
                );

        //check security context holder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        checkSecurityOperation(user, auth);

        FileDto fileDto = fileService.uploadSingleRestrictImage(file);

        //set course image
        user.setPhoto(fileDto.name());

        userRepository.save(user);

        return fileDto;
    }

    private @NonNull List<UserRole> updateUserRolesTransaction(@NonNull User user, @NonNull Set<Integer> roleIds) {
        List<UserRole> userRoles = new ArrayList<>();

        roleIds.forEach(roleId ->
                userRoles.add(UserRole.builder()
                        .user(user)
                        .role(Role.builder().id(roleId).build())
                        .build()));

        //set up new user roles
        user.setUserRoles(userRoles);

        return userRoles;
    }

    public void validateTransactionUserDto(TransactionUserDto transactionUserDto) {
        //check username if already exist
        if (userRepository.existsByUsernameIgnoreCase(transactionUserDto.username()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username conflicts resource in the system!");

        //check email if already exist
        if (userRepository.existsByEmailIgnoreCase(transactionUserDto.email()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email conflicts resource in the system!");

        //check phone number format
        if (!FormatUtil.checkPhoneFormat(transactionUserDto.phoneNumber()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Phone Number is not in a valid format!");

        //check phone number is already exist
        if (userRepository.existsByPhoneNumber(transactionUserDto.phoneNumber()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Phone Number conflicts resource in the system!");
    }

    public User setupNewUser(TransactionUserDto transactionUserDto) {
        //map from dto to entity
        User user = userMapper.fromTransactionUserDtoToUser(transactionUserDto);
        user.setUuid(UUID.randomUUID().toString());
        user.setVerified(false);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }

    private void checkSecurityOperation(User user, Authentication auth) {
        //if the user is ADMIN, allow the operation
        //if the user is not ADMIN, allow the operation, but only with security context authentication
        //meaning that author can update his or her own resource only
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        User authenticatedUser = customUserDetails.getUser();
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");

        if (customUserDetails.getAuthorities().contains(adminAuthority))
            return;

        if (!user.getUuid().equals(authenticatedUser.getUuid()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Permission denied!");
    }

    private boolean checkIfUserIsADMIN(User user) {
        List<UserRole> userRoles = user.getUserRoles();
        for (UserRole userRole : userRoles)
            if (userRole.getRole().getName().equals("ADMIN")) return true;
        return false;
    }

    private void checkAdminRemoveOperation(User user, Authentication auth) {
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        User authenticatedUser = customUserDetails.getUser();

        if (user.getUuid().equals(authenticatedUser.getUuid()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This is considered self-removed operation, permission denied!");
    }

}
