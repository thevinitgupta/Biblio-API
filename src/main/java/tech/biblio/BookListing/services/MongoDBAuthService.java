package tech.biblio.BookListing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.entities.AuthenticationUser;
import tech.biblio.BookListing.mappers.RoleAuthorityMapper;
import tech.biblio.BookListing.repositories.AuthenticationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MongoDBAuthService implements UserDetailsService {
    @Autowired
    AuthenticationRepository authenticationRepository;

    public AuthenticationUser addUser(AuthenticationUser authenticationUser){
        return authenticationRepository.save(authenticationUser);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorityList = null;
        AuthenticationUser user = null;
        List<AuthenticationUser> users = authenticationRepository.findByUsername(username);
        if (users.isEmpty())
            throw new UsernameNotFoundException("User Details not found for user : "
                    + username);
        else {
            user = users.get(0);
            authorityList = new ArrayList<>(RoleAuthorityMapper.rolesToAuthority(user.getRoles(), new ArrayList<>()));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorityList);

        /*
        * For Setting Authorities
        * user.getAuthorities()
          .forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole().getName())));
        */
    }
}
