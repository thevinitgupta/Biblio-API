package tech.biblio.BookListing.services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.entities.User;
import tech.biblio.BookListing.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User addUser(User user){
        return userRepository.save(user);
    }

    public  List<User> getAll(){
        return userRepository.findAll();
    }

    public List<User> getAllByFirstName(String firstName){
        return userRepository.findByFirstName(firstName);
    }

    public User getUserByEmail(String email) {
        return  userRepository.findFirstByEmail(email);
    }
}
