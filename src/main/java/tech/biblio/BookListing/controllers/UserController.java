package tech.biblio.BookListing.controllers;

import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.entities.User;
import tech.biblio.BookListing.services.PostService;
import tech.biblio.BookListing.services.UserService;

import java.util.List;


@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers(){
        try {
            List<User> users = userService.getAll();
            if(users.isEmpty()) {
                return new ResponseEntity<>("No Users Present", HttpStatus.FOUND);
            }
            return new ResponseEntity<List<User>>(users, HttpStatus.FOUND);
        }catch (Exception e){
            String message = e instanceof MongoException ? "Error in MongoDB" : "Server Error";
            System.out.println(e.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public  ResponseEntity<?> addUser(@RequestBody User user){
        try {
            User savedUser = userService.addUser(user);
            StringBuilder saveMessage = new StringBuilder()
                    .append("User with email : ")
                    .append(savedUser.getEmail())
                    .append(" saved successfully");
            return new ResponseEntity<>(saveMessage, HttpStatus.CREATED);
        }catch (Exception e){
            String message = e instanceof MongoException ? "Error Saving in MongoDB" : "Server Error";
            System.out.println(e.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
