package tech.biblio.BookListing.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.entities.AuthenticationUser;
import tech.biblio.BookListing.exceptions.UserNotFoundException;
import tech.biblio.BookListing.mappers.RoleAuthorityMapper;
import tech.biblio.BookListing.repositories.AuthenticationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;


    private final PasswordEncoder passwordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails user = userDetailsService.loadUserByUsername(username);
        if(null == user){
            throw new UserNotFoundException("No User found with Username");
        }
        else {
//            AuthenticationUser dbUser = user.;
            if(passwordEncoder.matches(password, user.getPassword())){
                List<GrantedAuthority> authorities = new ArrayList<>(
                        user.getAuthorities());
                return new UsernamePasswordAuthenticationToken(username,password,authorities);
            }
            else {
                throw new BadCredentialsException("Wrong Password");
            }
        }
    }

    /**
     * Checks if the Authentication Type is Supported in the Provider
    * @param authentication object
    */
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
