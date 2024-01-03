package com.vicheak.coreapp.api.user;

import com.vicheak.coreapp.api.user.web.TransactionUserDto;
import com.vicheak.coreapp.api.user.web.UserDto;
import com.vicheak.coreapp.util.ValueInjectUtil;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    protected ValueInjectUtil valueInjectUtil;

    @Autowired
    public void setValueInjectUtil(ValueInjectUtil valueInjectUtil) {
        this.valueInjectUtil = valueInjectUtil;
    }

    @Mapping(target = "isVerified", source = "verified")
    @Mapping(target = "isEnabled", source = "enabled")
    @Mapping(target = "photoUri", expression = "java(valueInjectUtil.getImageUri(user.getPhoto()))")
    public abstract UserDto fromUserToUserDto(User user);

    public abstract List<UserDto> fromUserToUserDto(List<User> users);

    public abstract User fromTransactionUserDtoToUser(TransactionUserDto transactionUserDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void fromTransactionUserDtoToUser(@MappingTarget User user, TransactionUserDto transactionUserDto);

}
