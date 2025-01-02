package com.tiny.bank.config;

import com.tiny.bank.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    UserRepository userRepository() {
        return new UserRepository();
    }

}
