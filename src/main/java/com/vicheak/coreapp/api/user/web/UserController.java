package com.vicheak.coreapp.api.user.web;

import com.vicheak.coreapp.api.file.web.FileDto;
import com.vicheak.coreapp.api.user.UserService;
import com.vicheak.coreapp.base.BaseApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public BaseApi<?> loadAllUsers() {

        List<UserDto> userDtoList = userService.loadAllUsers();

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("All users loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(userDtoList)
                .build();
    }

    @GetMapping("/{uuid}")
    public BaseApi<?> loadUserByUuid(@PathVariable String uuid) {

        UserDto userDto = userService.loadUserByUuid(uuid);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("User with uuid, %s loaded successfully!".formatted(uuid))
                .timestamp(LocalDateTime.now())
                .payload(userDto)
                .build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createNewUser(@RequestBody @Valid TransactionUserDto transactionUserDto) {
        userService.createNewUser(transactionUserDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{uuid}")
    public void updateUserByUuid(@PathVariable String uuid,
                                 @RequestBody TransactionUserDto transactionUserDto) {
        userService.updateUserByUuid(uuid, transactionUserDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{uuid}")
    public void deleteUserByUuid(@PathVariable String uuid) {
        userService.deleteUserByUuid(uuid);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{uuid}")
    public void updateUserIsEnabledByUuid(@PathVariable String uuid,
                                          @RequestBody @Valid IsEnabledDto isEnabledDto) {
        userService.updateUserIsEnabledByUuid(uuid, isEnabledDto.isEnabled());
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/uploadImage/{uuid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDto uploadUserPhotoByUuid(@PathVariable String uuid,
                                         @RequestPart MultipartFile file) {
        return userService.uploadUserPhotoByUuid(uuid, file);
    }

    @GetMapping("/me")
    public BaseApi<?> loadUserProfile(Authentication authentication) {

        UserDto userDto = userService.loadUserProfile(authentication);

        return BaseApi.builder()
                .isSuccess(true)
                .code(HttpStatus.OK.value())
                .message("User profile loaded successfully!")
                .timestamp(LocalDateTime.now())
                .payload(userDto)
                .build();
    }

}
