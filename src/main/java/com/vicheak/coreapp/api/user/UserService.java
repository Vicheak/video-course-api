package com.vicheak.coreapp.api.user;

import com.vicheak.coreapp.api.file.web.FileDto;
import com.vicheak.coreapp.api.user.web.TransactionUserDto;
import com.vicheak.coreapp.api.user.web.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    /**
     * This method is used to load all users from the system
     * @return List<UserDto>
     */
    List<UserDto> loadAllUsers();

    /**
     * This method is used to load specific user by uuid
     * @param uuid is the path parameter from client
     * @return UserDto
     */
    UserDto loadUserByUuid(String uuid);

    /**
     * This method is used to create new user (ADMIN authority)
     * @param transactionUserDto is the request from client
     */
    void createNewUser(TransactionUserDto transactionUserDto);

    /**
     * This method is used to update specific user by uuid
     * @param uuid is the path parameter from client
     * @param transactionUserDto is the request from client
     */
    void updateUserByUuid(String uuid, TransactionUserDto transactionUserDto);

    /**
     * This method is used to delete specific user by uuid from the system
     * @param uuid is the path parameter from client
     */
    void deleteUserByUuid(String uuid);

    /**
     * This method is used to disable specific user by uuid from the system
     * @param uuid is the path parameter from client
     * @param isEnabled is the request from client
     */
    void updateUserIsEnabledByUuid(String uuid, Boolean isEnabled);

    /**
     * This method is used to upload specific user photo by uuid
     * @param uuid is the path parameter from client
     * @param file is the request path from client
     * @return FileDto
     */
    FileDto uploadUserPhotoByUuid(String uuid, MultipartFile file);

}
