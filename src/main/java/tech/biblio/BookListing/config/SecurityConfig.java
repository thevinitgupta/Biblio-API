package tech.biblio.BookListing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    private UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager customAuthenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // Disabling CSRF
         http.csrf(AbstractHttpConfigurer::disable);
//        http.csrf(httpSecurityCsrfConfigurer ->
//                httpSecurityCsrfConfigurer
//                        .csrfTokenRepository(
//                                CookieCsrfTokenRepository.withHttpOnlyFalse()
//                        ));
        http.cors(AbstractHttpConfigurer::disable).authorizeHttpRequests((requests) -> {
//            requests.anyRequest().permitAll();
            requests.requestMatchers("/health").permitAll();
            requests.requestMatchers("/auth/**").permitAll();
            requests.requestMatchers("/user/**").authenticated();
            requests.requestMatchers("/posts/**").authenticated();
        });
        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();
    }
}

