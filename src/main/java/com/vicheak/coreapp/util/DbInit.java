package com.vicheak.coreapp.util;

import com.vicheak.coreapp.api.authority.Authority;
import com.vicheak.coreapp.api.authority.AuthorityRepository;
import com.vicheak.coreapp.api.authority.Role;
import com.vicheak.coreapp.api.authority.RoleRepository;
import com.vicheak.coreapp.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DbInit {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

//    @PostConstruct
    public void dbSetUp(){
        Role adminRole = Role.builder()
                .name("ADMIN")
                .build();

        Role authorRole = Role.builder()
                .name("AUTHOR")
                .build();

        Role subscriberRole = Role.builder()
                .name("SUBSCRIBER")
                .build();

        Set<Authority> categoryAuthorities = Set.of(
                Authority.builder().name("category:read").build(),
                Authority.builder().name("category:write").build(),
                Authority.builder().name("category:update").build(),
                Authority.builder().name("category:delete").build()
        );

        Set<Authority> courseAuthorities = Set.of(
                Authority.builder().name("course:read").build(),
                Authority.builder().name("course:write").build(),
                Authority.builder().name("course:update").build(),
                Authority.builder().name("course:delete").build()
        );
    }

}
