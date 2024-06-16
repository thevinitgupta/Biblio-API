package tech.biblio.BookListing.controllers;

import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tech.biblio.BookListing.entities.AuthenticationUser;
import tech.biblio.BookListing.entities.User;
import tech.biblio.BookListing.mappers.UserMapper;
import tech.biblio.BookListing.services.MongoDBAuthService;
import tech.biblio.BookListing.services.UserService;

@Controller
@RequestMapping("auth")
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    MongoDBAuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("register")
    public ResponseEntity<?> register(){
        return new ResponseEntity<>("Cannot Get Here. Only POST Allowed", HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("register")
    public ResponseEntity<?> addUser(@RequestBody User user){
        User savedUser = null;
        if(user==null) {
            return new ResponseEntity<>("No User Passed", HttpStatus.NO_CONTENT);
        }
        try {
            AuthenticationUser authenticationUser = UserMapper.authUser(user,"", passwordEncoder);
            savedUser = userService.addUser(user);
            AuthenticationUser savedAuthUser = authService.addUser(authenticationUser);
            if(savedAuthUser==null) throw new AuthorizationServiceException("User not created.");
            StringBuilder saveMessage = new StringBuilder()
                    .append("User with email : ")
                    .append(savedUser.getEmail())
                    .append(" saved successfully");
            return new ResponseEntity<>(saveMessage, HttpStatus.CREATED);
        }catch (AuthorizationServiceException authException){
            userService.deleteUser(savedUser);
            return new ResponseEntity<>("User Not Registered", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (DuplicateKeyException e){
            return new ResponseEntity<>("User with Email Already Exists",
                    HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            System.out.println(e.getClass());
//            if(userService.getUserByEmail(user.getEmail())!=null)
                userService.deleteUser(savedUser);
            String message = e instanceof MongoException ? "Error Saving in MongoDB" : "Server Error";
            System.out.println(e.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
