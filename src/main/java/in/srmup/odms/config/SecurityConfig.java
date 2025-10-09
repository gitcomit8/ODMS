package in.srmup.odms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Allow access to H2 console, dev-login and static resources
                        .requestMatchers("/h2-console/**", "/dev-login", "/login", "/generate-otp", "/login-with-otp", "/css/**", "/js/**", "/images/**").permitAll()
                        // Require authentication for approver endpoints
                        .requestMatchers("/approver/**").authenticated()
                        // For development, allow all other requests
                        .anyRequest().permitAll()
                )
                // Enable CSRF protection except for H2 console and dev-login
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/dev-login")
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()) // Allow H2 console frames
                )
                // Configure custom login
                .formLogin(form -> form
                        .loginPage("/dev-login")
                        .loginProcessingUrl("/perform-login")  // Use different URL for Spring Security login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        System.out.println("INSIDE SECURITY CONFIG");

        return http.build();
    }
}