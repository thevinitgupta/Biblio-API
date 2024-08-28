package tech.biblio.BookListing.controllers;

import com.mongodb.MongoException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.biblio.BookListing.contants.ApplicationConstants;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.AuthenticationUser;
import tech.biblio.BookListing.entities.LoginRequestDTO;
import tech.biblio.BookListing.entities.LoginResponseDTO;
import tech.biblio.BookListing.entities.User;
import tech.biblio.BookListing.mappers.UserMapper;
import tech.biblio.BookListing.services.MongoDBAuthService;
import tech.biblio.BookListing.services.RoleService;
import tech.biblio.BookListing.services.UserService;
import org.springframework.core.env.Environment;
import tech.biblio.BookListing.utils.JwtUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Controller
@RequestMapping("auth")
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    MongoDBAuthService authService;

    @Autowired
    RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private Environment env;

    @Autowired
    private JwtUtils jwtUtils;


    @GetMapping("register")
    public ResponseEntity<?> register(){
        return new ResponseEntity<>("Cannot Get Here. Only POST Allowed", HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest){
        UserDTO dbUser = null;
        String jwtToken = null;

        if(loginRequest==null){
            return new ResponseEntity<>(
                    new LoginResponseDTO(HttpStatus.BAD_REQUEST.getReasonPhrase(),"Credentials missing",null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            dbUser = userService.getUserByEmail(loginRequest.email());
            if(null==dbUser) {
                return new ResponseEntity<>(
                        new LoginResponseDTO(HttpStatus.NOT_FOUND.getReasonPhrase(),"No user with email exists",null),
                        HttpStatus.NOT_FOUND);
            }

            Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
                    loginRequest.email(),
                    loginRequest.password());

            Authentication authenticationResponse = authenticationManager.authenticate(authentication);

            if(null != authenticationResponse && authenticationResponse.isAuthenticated()){
                if(env==null) throw new Exception();
                if(env!=null){
                    String secret = env.getProperty(ApplicationConstants.JWT_SECRET, ApplicationConstants.JWT_SECRET_DEFAULT);
                    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                    jwtToken = jwtUtils.generateJwtToken(authenticationResponse);
                    System.out.println(jwtToken);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(
                    HttpStatus.OK.getReasonPhrase(),
                    "Login Successfull",
                    jwtToken
            ));

        }catch (BadCredentialsException e){
            return new ResponseEntity<>(
                    new LoginResponseDTO(
                            HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            e.getLocalizedMessage(),
                            ""
                    ), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }catch (Exception e){
            return new ResponseEntity<>(
                    new LoginResponseDTO(
                            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            e.getLocalizedMessage(),
                            ""
                    ), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

//        return new ResponseEntity<>(new LoginResponseDTO(HttpStatus.OK.toString(),"Login Successfull", ""),HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<?> addUser(@RequestBody User user){
        User savedUser = null;
        if(user==null) {
            return new ResponseEntity<>("No User Passed", HttpStatus.NO_CONTENT);
        }
        try {
            user.setRoles(roleService.getRoles("ROLE_USER"));
            AuthenticationUser authenticationUser = UserMapper.authUser(user,roleService.getRoles("ROLE_USER"), passwordEncoder);
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
            if(savedUser!=null) {
                userService.deleteUser(savedUser);
            }
            String message = e instanceof MongoException ? "Error Saving in MongoDB" : "Server Error";
            System.out.println(e.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
