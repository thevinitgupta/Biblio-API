package tech.biblio.BookListing.mappers;

import org.springframework.security.crypto.password.PasswordEncoder;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.AuthenticationUser;
import tech.biblio.BookListing.entities.Role;
import tech.biblio.BookListing.entities.User;

import java.util.Collection;

public class UserMapper {
    public static UserDTO userDTO(User user, boolean allowPosts) {
        return new UserDTO(user.getEmail(), user.getFirstName(), user.getLastName(), allowPosts ? user.getPosts() : null, user.isProfileImageAdded(), user.getProfileImageId());
    }

    public static User userEntity(UserDTO userDTO, User dbUser) {
        User user = User.builder()
                .id(dbUser.getId())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .password(dbUser.getPassword())
                .posts(dbUser.getPosts())
                .build();
        System.out.println("User Entity : " + user);
        return user;
    }

    public static AuthenticationUser authUser(
            User user,
            Collection<Role> roles,
            PasswordEncoder passwordEncoder) {
        AuthenticationUser authenticationUser = new AuthenticationUser();
        authenticationUser.setUsername(user.getEmail());
        authenticationUser.setPassword(passwordEncoder.encode(user.getPassword()));
        authenticationUser.setRoles(roles);
        return authenticationUser;
    }
}
