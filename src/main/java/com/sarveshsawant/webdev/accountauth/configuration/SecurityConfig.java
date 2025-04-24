package com.sarveshsawant.webdev.accountauth.configuration;


import com.sarveshsawant.webdev.accountauth.service.UserService;
import com.sarveshsawant.webdev.dbhealthcheck.service.DatabaseHealthCheckService;
import com.sarveshsawant.webdev.util.MethodFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    DatabaseHealthCheckService databaseHealthCheckService;

    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, DatabaseHealthCheckService databaseHealthCheckService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.databaseHealthCheckService = databaseHealthCheckService;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .addFilterBefore(new MethodFilter(databaseHealthCheckService, userService), SecurityContextHolderFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/user", "/healthz", "/v1/verify").permitAll()
                        .requestMatchers("/v1/user/self").authenticated()
                        .requestMatchers("/v1/user/self/pic").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.out.println("Inside httpBasic authenticationEntryPoint");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentLength(0);
                        })
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
}
