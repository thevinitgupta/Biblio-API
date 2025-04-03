package tech.biblio.BookListing.controllers;

import com.mongodb.MongoException;
import io.appwrite.exceptions.AppwriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.biblio.BookListing.dto.PostDTO;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.exceptions.FileTypeNotAllowedException;
import tech.biblio.BookListing.exceptions.PostNotFoundException;
import tech.biblio.BookListing.mappers.PostMapper;
import tech.biblio.BookListing.services.UserService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers(@CookieValue(name = "refreshToken", defaultValue = "") String refreshToken){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println(authentication.getName());
        log.info("User Logged In with Email {}",authentication.getName());
        try {
            String email = authentication.getName();
//            Cookie[] cookies =
            System.out.println("Cookies in user contrl : \n"+ refreshToken);
            UserDTO user = userService.getUserByEmail(email, false);
            if(user==null) {
                return new ResponseEntity<>("No Users Present with Email", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch (MongoException e){
            String message = e instanceof MongoException ? "Error in MongoDB" : "Server Error";
            System.out.println(e.getLocalizedMessage());
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/{email}")
    public ResponseEntity<?> getUsersByEmail(@PathVariable String email){
        try {
            UserDTO user = userService.getUserByEmail(email, false);

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

    @GetMapping("/profileImage")
    public ResponseEntity<?> getProfileImage() throws AppwriteException, ExecutionException, InterruptedException {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String email = authentication.getName();
            UserDTO user = userService.getUserByEmail(email, false);

            if(user==null) {
                return new ResponseEntity<>("No Users Present with Email", HttpStatus.FOUND);
            }
            byte [] fileBytes = userService.getUserProfileImage(user);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(email.split("@")[0].replace(".","_")+"_profile_img.png").build());
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @PostMapping("/profileImage")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile multipartFile) throws AppwriteException, IOException, ExecutionException, InterruptedException, FileTypeNotAllowedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean imageUploaded = userService.uploadProfileImage(multipartFile, email);
        if(imageUploaded){
            return new ResponseEntity<>("Successfully Uploaded",HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Image Upload Failed!",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/profileImage")
    public ResponseEntity<?> deleteProfileImage() throws AppwriteException, IOException, ExecutionException, InterruptedException, FileTypeNotAllowedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        UserDTO dbUser = userService.getUserByEmail(email, false);

        if(!dbUser.isProfileImageAdded()){
            return new ResponseEntity<>("Profile Image doesn't exist",HttpStatus.BAD_REQUEST);
        }

        boolean imageDeleted = userService.deleteProfileImage(dbUser);
        if(imageDeleted){
            return new ResponseEntity<>("Successfully Deleted",HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Image Delete Failed!",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPostsByUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        UserDTO user = userService.getUserByEmail(email, true);
        List<Post> userPosts = user.getPosts();
        if (userPosts == null || userPosts.isEmpty()) {
            throw new PostNotFoundException("No Posts found for User", user.getFirstName());
        }

        List<PostDTO> userPostsDTO = userPosts.stream()
                .map((PostMapper::postDTO)).toList();

        return new ResponseEntity<>(userPostsDTO, HttpStatus.OK);
    }
}
