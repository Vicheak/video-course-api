package com.vicheak.coreapp.config;

import com.vicheak.coreapp.api.user.User;
import com.vicheak.coreapp.security.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<User> auditorProvider(){
        return new AuditorAwareImpl();
    }

}
