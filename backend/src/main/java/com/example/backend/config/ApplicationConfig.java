package com.example.backend.config;

import com.example.backend.exceptions.EntityNotFoundException;
import com.example.backend.exceptions.ErrorCodes;
import com.example.backend.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final AdminRepository adminRepository;
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> adminRepository.findByUsername(username)
                .orElseThrow(()->new EntityNotFoundException(
                        "Username " + username+" not found.",
                        ErrorCodes.ADMIN_NOT_FOUND
                ));
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passworderEncoder());
        return authProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passworderEncoder() {
        return new BCryptPasswordEncoder();
    }

}
