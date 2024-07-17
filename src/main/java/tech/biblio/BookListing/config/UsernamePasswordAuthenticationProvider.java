package tech.biblio.BookListing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.entities.AuthenticationUser;
import tech.biblio.BookListing.mappers.RoleAuthorityMapper;
import tech.biblio.BookListing.repositories.AuthenticationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        List<AuthenticationUser> users = authenticationRepository.findByUsername(username);
        if(users.isEmpty()){
            throw new BadCredentialsException("No User found with Username");
        }
        else {
            AuthenticationUser dbUser = users.get(0);
            if(passwordEncoder.matches(password, dbUser.getPassword())){
                List<GrantedAuthority> authorities = new ArrayList<>(
                        RoleAuthorityMapper.rolesToAuthority(dbUser.getRoles(),
                                new ArrayList<>()));
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
