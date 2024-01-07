package com.vicheak.coreapp.security;

import com.vicheak.coreapp.api.user.User;
import com.vicheak.coreapp.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User authenticatedUser = userRepository.findByEmailAndVerifiedTrueAndEnabledTrue(username)
                .orElseThrow(
                        () -> {
                            log.error("Email has not been authenticated!");
                            return new UsernameNotFoundException("Email has not been authenticated!");
                        }
                );

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setUser(authenticatedUser);

        //log.info("Auth email : {}", customUserDetails.getUsername());
        //log.info("Auth authorities : {}", customUserDetails.getAuthorities());

        return customUserDetails;
    }

}
