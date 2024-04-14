package tech.biblio.BookListing.controllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.entities.User;
import tech.biblio.BookListing.exceptions.UserNotFoundException;
import tech.biblio.BookListing.services.PostService;
import tech.biblio.BookListing.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAll(){
        try {
        return new ResponseEntity<List<Post>>(postService.getAll(), HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getClass() + ", "+ e.getMessage()  );
            String message = e instanceof UncategorizedMongoDbException ? "Database Error" : e.getLocalizedMessage();
            return new ResponseEntity<>(message,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("{email}")
    public ResponseEntity<?> createPost(@RequestBody Post post, @PathVariable String email){
        System.out.println(post.toString());
        try {
            Post savedPost = postService.addPost(email,post);
            return new ResponseEntity<Post>(savedPost, HttpStatus.CREATED);
        }catch (UserNotFoundException e){
            System.out.println(e.getLocalizedMessage());
            String message = "User with Email "+ email+" does not Exist";
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            System.out.println(e.getClass() + ", "+ e.getMessage()  );
            String message = e instanceof UncategorizedMongoDbException ? "Database Error" : e.getLocalizedMessage();
            return new ResponseEntity<>(message,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{email}")
    public ResponseEntity<?> getPostsForUser(@PathVariable String email){
        try {
            User user = userService.getUserByEmail(email);
            if(user==null) throw new UserNotFoundException("");
            List<Post> userPosts = user.getPosts();
            if(userPosts!=null && !userPosts.isEmpty()) {
                return new ResponseEntity<>(userPosts, HttpStatus.FOUND);
            }
            return new ResponseEntity<>("No Posts found for "+user.getFirstName(), HttpStatus.NOT_FOUND);
        }catch (UserNotFoundException e){
            System.out.println(e.getLocalizedMessage());
            String message = "User with Email "+ email+" does not Exist";
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            System.out.println(e.getClass() + ", "+ e.getMessage()  );
            String message = e instanceof UncategorizedMongoDbException ? "Database Error" : e.getLocalizedMessage();
            return new ResponseEntity<>(message,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
