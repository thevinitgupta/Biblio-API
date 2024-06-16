package tech.biblio.BookListing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.User;
import tech.biblio.BookListing.mappers.UserMapper;
import tech.biblio.BookListing.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User addUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public User updateUser(UserDTO user){
        User dbUser = userRepository.findFirstByEmail(user.getEmail());
        dbUser.setPosts(user.getPosts());
        return userRepository.save(dbUser);
    }

    public  List<UserDTO> getAll(){
        return userRepository.findAll().stream().map(user -> UserMapper.userDTO(user,false)).toList();
    }

    public List<UserDTO> getAllByFirstName(String firstName){
        return userRepository.findByFirstName(firstName).stream().map(user -> UserMapper.userDTO(user,false)).toList();
    }

    public UserDTO getUserByEmail(String email) {
        return  UserMapper.userDTO(userRepository.findFirstByEmail(email), false);
    }

    public void deleteUser(User user){
        userRepository.deleteById(user.getId());
    }
}
