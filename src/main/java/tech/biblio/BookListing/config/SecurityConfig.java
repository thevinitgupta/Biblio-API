package tech.biblio.BookListing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import tech.biblio.BookListing.filters.*;

import java.util.List;

@Configuration
@EnableMethodSecurity(
//        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
@EnableWebSecurity(
        debug = true
)
public class SecurityConfig {
    private UserDetailsService userDetailsService;
    @Autowired
    private GlobalExceptionHandlingFilter globalExceptionHandlingFilter;

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
                            config.setAllowedHeaders(List.of("*"));
                            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                            config.setAllowCredentials(true);
                            config.setExposedHeaders(List.of(HttpHeaders.SET_COOKIE,"X-CSRF-TOKEN","_csrf")); // Expose Set-Cookie header to the client
                            config.setMaxAge(3600L);
                            return config;
                        }
                ));


        // Disabling CSRF
         // http.csrf(AbstractHttpConfigurer::disable);

        http.csrf(httpSecurityCsrfConfigurer ->
                httpSecurityCsrfConfigurer.csrfTokenRequestHandler(csrfTokenRequestHandler)

                .ignoringRequestMatchers(HttpMethod.OPTIONS.name(),"/auth/register", "/auth/login", "auth/access-token")
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse()
                        ));
        http.addFilterBefore(globalExceptionHandlingFilter, LogoutFilter.class);

        http.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
        http.addFilterBefore(new RequestValidationFilter(), BasicAuthenticationFilter.class);

// Corrected Order for JWT Filters
        http.addFilterAfter(new JWTValidationFilter(), RequestValidationFilter.class); // JWTValidation runs right after RequestValidation
        http.addFilterAfter(new AuthoritiesLoggingFilter(), JWTValidationFilter.class); // AuthoritiesLogging runs after JWTValidation
        http.addFilterAfter(new JWTGenerationFilter(), AuthoritiesLoggingFilter.class); // JWTGeneration runs after AuthoritiesLogging

// For Refresh Token
        http.addFilterAfter(new RefreshTokenValidationFilter(), JWTGenerationFilter.class); // Ensure it runs after JWT Generation



        http.authorizeHttpRequests((requests) -> {
//            requests.anyRequest().permitAll();
            requests.requestMatchers("/health").permitAll();
            requests.requestMatchers(HttpMethod.POST,"/auth/register").permitAll();
            requests.requestMatchers(HttpMethod.POST,"/auth/login").permitAll();
            requests.requestMatchers(HttpMethod.GET,"/auth/access-token").permitAll();
            requests.requestMatchers("/user/**").authenticated();
//            requests.requestMatchers("/user/**").hasAnyAuthority(Privilege.CREATE_USER.getPrivilege());

            requests.requestMatchers("/posts/**").authenticated();

            requests.requestMatchers("/admin/**").authenticated();
        });
//        http.formLogin(withDefaults());
//        http.httpBasic(withDefaults());
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

