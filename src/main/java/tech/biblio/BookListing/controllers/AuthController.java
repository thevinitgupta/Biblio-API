package tech.biblio.BookListing.controllers;

import com.mongodb.MongoException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import tech.biblio.BookListing.dto.LoginRequestDTO;
import tech.biblio.BookListing.dto.LoginResponseDTO;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.AuthenticationUser;
import tech.biblio.BookListing.entities.RefreshTokenStore;
import tech.biblio.BookListing.entities.User;
import tech.biblio.BookListing.exceptions.RefreshTokenValidationException;
import tech.biblio.BookListing.mappers.UserMapper;
import tech.biblio.BookListing.services.MongoDBAuthService;
import tech.biblio.BookListing.services.RefreshTokenService;
import tech.biblio.BookListing.services.RoleService;
import tech.biblio.BookListing.services.UserService;
import tech.biblio.BookListing.utils.JwtUtils;
import tech.biblio.BookListing.utils.UniqueID;

import java.util.Date;
import java.util.HashMap;
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
    RefreshTokenService refreshTokenService;

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
        return new ResponseEntity<>("Cannot Get Here. Only POST Allowed",
                HttpStatus.NOT_IMPLEMENTED);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest,
                                                  HttpServletResponse httpResponse){
        UserDTO dbUser = null;
        String accessToken = null;
        String refreshToken = null;
        String refreshTokenID = null;

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
                if(env==null) throw new NullPointerException();
                HashMap<String, Object> accessTokenClaims = new HashMap<>();
                accessTokenClaims.put("username", authenticationResponse.getName());
                accessTokenClaims.put("authorities", authenticationResponse.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")));
                accessToken = jwtUtils.generateAccessToken(accessTokenClaims, env);
                HashMap<String, Object> refreshTokenClaimsMap = new HashMap<>();
                refreshTokenID = UniqueID.generateId();
                refreshTokenClaimsMap.put("username", authenticationResponse.getName());
                refreshTokenClaimsMap.put("token-id",refreshTokenID);
                refreshToken = jwtUtils.generateRefreshToken(refreshTokenClaimsMap, env);

                String setCookieHeader = String.format(
                        "refreshToken=%s; HttpOnly; Secure; Path=/; Max-Age=%d; SameSite=None",
                        refreshToken,
                        7 * 24 * 60 * 60 // 7 days expiration
                );

                // Register Refresh Token in Repository
                RefreshTokenStore refreshTokenStore = RefreshTokenStore.builder()
                        .tokenId(refreshTokenID)
                        .refreshToken(refreshToken)
                        .username(loginRequest.email())
                        .issuedAt(new Date(System.currentTimeMillis()))
                        .isValid(true)
                        .build();

                refreshTokenService.saveToken(refreshTokenStore);

                httpResponse.setHeader("Set-Cookie", setCookieHeader);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new LoginResponseDTO(
                        HttpStatus.OK.getReasonPhrase(),
                        "Login Successfull",
                        accessToken
                ));
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponseDTO(
                            HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            "Please check credentials",
                            accessToken
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

//        return new ResponseEntity<>(new LoginResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.toString(),"Something Went Wrong", ""), HttpStatus.INTERNAL_SERVER_ERROR);
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

    @GetMapping("access-token")
    public ResponseEntity<?> generateAccessToken(@CookieValue(name = "refreshToken", defaultValue = "") String refreshToken, HttpServletRequest request){
        try {
            if(refreshToken==null || refreshToken.isEmpty()) throw new RefreshTokenValidationException("No Refresh Token found");
            if(env!=null){
                boolean validateTokenFormat = jwtUtils.validateRefreshToken(refreshToken, env);
                final Claims claimsFromJwt = jwtUtils.getClaimsFromJwt(refreshToken, env);
                boolean validateDbToken = refreshTokenService.checkValidity(
                        claimsFromJwt.get("token-id", String.class));
                /*
                 COMPLETED : 1. Remove User details being passed to Token Generators, pass only Username and if Required Authorities
                 COMPLETED : 2. Add access token generation
                */
                if(validateTokenFormat && validateDbToken){
                    String accessToken = "";
                    UserDTO user = userService.getUserByEmail(claimsFromJwt.get("username", String.class));

                    HashMap<String, Object> accessTokenClaims = new HashMap<>();
                    accessTokenClaims.put("username", user.getEmail());
                    accessTokenClaims.put("authorities", userService.getUserAuthorities(user.getEmail())
                            .stream().map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(",")));

                    accessToken = jwtUtils.generateAccessToken(accessTokenClaims, env);
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new LoginResponseDTO(
                                    HttpStatus.OK.getReasonPhrase(),
                                    "Login Successfull",
                                    accessToken
                            ));
                }
            }

        }finally {

        }
        return null;
    }

}
