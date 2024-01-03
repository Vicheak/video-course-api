package com.vicheak.coreapp.api.authority;

import com.vicheak.coreapp.api.authority.web.RoleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto fromRoleToRoleDto(Role role);

}
