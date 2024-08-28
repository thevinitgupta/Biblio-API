package tech.biblio.BookListing.config;

import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import tech.biblio.BookListing.entities.Privilege;
import tech.biblio.BookListing.entities.Role;
import tech.biblio.BookListing.filters.*;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(
//        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfig {
    private UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager customAuthenticationManager(HttpSecurity http)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
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
        CsrfTokenRequestAttributeHandler csrfTokenRequestHandler =
                new CsrfTokenRequestAttributeHandler();
        csrfTokenRequestHandler.setCsrfRequestAttributeName("_csrf");

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        http.cors(AbstractHttpConfigurer::disable);

        http.cors(
                httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(request ->
                        {
                            CorsConfiguration config = new CorsConfiguration();
                            config.addAllowedOrigin("http://localhost:3000");
//                            config.addAllowedOrigin("http://localhost:3000");
                            config.addAllowedHeader("*");
                            config.addAllowedMethod("*");
                            config.setAllowCredentials(true);
                            config.setMaxAge(3600L);
                            return config;
                        }
                ));

        // Disabling CSRF
         // http.csrf(AbstractHttpConfigurer::disable);

        http.csrf(httpSecurityCsrfConfigurer ->
                httpSecurityCsrfConfigurer.csrfTokenRequestHandler(csrfTokenRequestHandler)
                .ignoringRequestMatchers("/auth/register", "/auth/login")
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        ))
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
                http.addFilterBefore(new RequestValidationFilter(), BasicAuthenticationFilter.class);
                http.addFilterAfter(new AuthoritiesLoggingFilter(), BasicAuthenticationFilter.class);

                // For JWT Tokens
                http.addFilterBefore(new JWTValidationFilter(), BasicAuthenticationFilter.class);
                http.addFilterAfter(new JWTGenerationFilter(), BasicAuthenticationFilter.class);


                http.authorizeHttpRequests((requests) -> {
//            requests.anyRequest().permitAll();
            requests.requestMatchers("/health").permitAll();
            requests.requestMatchers(HttpMethod.POST,"/auth/register").permitAll();
            requests.requestMatchers(HttpMethod.POST,"/auth/login").permitAll();
            requests.requestMatchers("/user/**").authenticated();
//            requests.requestMatchers("/user/**").hasAnyAuthority(Privilege.CREATE_USER.getPrivilege());

            requests.requestMatchers("/posts/**").authenticated();

            requests.requestMatchers("/admin/**").authenticated();
        });
        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                PasswordEncoder passwordEncoder){
        UsernamePasswordAuthenticationProvider authenticationProvider = new
                UsernamePasswordAuthenticationProvider(userDetailsService,passwordEncoder);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }
}

