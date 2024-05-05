package tech.biblio.BookListing.mappers;

import org.springframework.security.crypto.password.PasswordEncoder;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.AuthenticationUser;
import tech.biblio.BookListing.entities.User;

public class UserMapper {
    public static  UserDTO userDTO(User user){
        return new UserDTO(user.getEmail(), user.getFirstName(), user.getLastName(), user.getPosts());
    }
    public static User userEntity(UserDTO userDTO, User dbUser){
        User user = new User(userDTO.getEmail(), userDTO.getFirstName(), dbUser.getPassword());
        user.setId(dbUser.getId());
        user.setPosts(dbUser.getPosts());
        System.out.println("User Entity : "+user);
        return user;
    }

    public static AuthenticationUser authUser(User user, String role, PasswordEncoder passwordEncoder){
        AuthenticationUser authenticationUser = new AuthenticationUser();
        authenticationUser.setUsername(user.getEmail());
        authenticationUser.setPassword(passwordEncoder.encode(user.getPassword()));
        authenticationUser.setRole(role==null || role.isEmpty() ? "USER" : role);
        return authenticationUser;
    }
}
