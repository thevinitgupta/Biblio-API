package tech.biblio.BookListing.mappers;

import org.springframework.security.crypto.password.PasswordEncoder;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.AuthenticationUser;
import tech.biblio.BookListing.entities.Role;
import tech.biblio.BookListing.entities.User;

import java.util.Collection;
import java.util.Set;

public class UserMapper {
    public static  UserDTO userDTO(User user, boolean allowPosts){
        return new UserDTO(user.getEmail(), user.getFirstName(), user.getLastName(), allowPosts ? user.getPosts() : null);
    }
    public static User userEntity(UserDTO userDTO, User dbUser){
        User user = new User(userDTO.getEmail(), userDTO.getFirstName(), dbUser.getPassword());
        user.setId(dbUser.getId());
        user.setPosts(dbUser.getPosts());
        System.out.println("User Entity : "+user);
        return user;
    }

    public static AuthenticationUser authUser(
            User user,
            Collection<Role> roles,
            PasswordEncoder passwordEncoder){
        AuthenticationUser authenticationUser = new AuthenticationUser();
        authenticationUser.setUsername(user.getEmail());
        authenticationUser.setPassword(passwordEncoder.encode(user.getPassword()));
        authenticationUser.setRoles(roles);
        return authenticationUser;
    }
}
