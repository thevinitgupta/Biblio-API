package tech.biblio.BookListing.controllers;

import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.services.UserService;

import java.util.List;


@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());
        String email = authentication.getName();
        try {
            UserDTO user = userService.getUserByEmail(email);
            if(user==null) {
                return new ResponseEntity<>("No Users Present with Email", HttpStatus.FOUND);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (Exception e){
            String message = e instanceof MongoException ? "Error in MongoDB" : "Server Error";
            System.out.println(e.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/{email}")
    public ResponseEntity<?> getUsersByEmail(@PathVariable String email){
        try {
            UserDTO user = userService.getUserByEmail(email);
            if(user==null) {
                return new ResponseEntity<>("No Users Present with Email", HttpStatus.FOUND);
            }
            return new ResponseEntity<>(user, HttpStatus.FOUND);
        }catch (Exception e){
            String message = e instanceof MongoException ? "Error in MongoDB" : "Server Error";
            System.out.println(e.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
