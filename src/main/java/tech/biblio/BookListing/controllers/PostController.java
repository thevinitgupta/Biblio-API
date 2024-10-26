package tech.biblio.BookListing.controllers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.biblio.BookListing.dto.CreatePostDTO;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.exceptions.PostNotFoundException;
import tech.biblio.BookListing.exceptions.UserNotFoundException;
import tech.biblio.BookListing.services.PostService;
import tech.biblio.BookListing.services.UserService;

import java.util.List;


@RestController
@RequestMapping("posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

//    @GetMapping
//    public ResponseEntity<?> getAll(){
//        try {
//        return new ResponseEntity<List<Post>>(postService.getAll(), HttpStatus.OK);
//        }catch (Exception e){
//            System.out.println(e.getClass() + ", "+ e.getMessage()  );
//            String message = e instanceof UncategorizedMongoDbException ? "Database Error" : e.getLocalizedMessage();
//            return new ResponseEntity<>(message,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody CreatePostDTO createPostDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Post post = Post.builder()
                        .title(createPostDTO.title())
                        .content(createPostDTO.content())
                        .likes(0)
                        .comments(new String[]{})
                        .build();
        System.out.println(post.toString());
        Post savedPost = postService.addPost(email,post);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<?> getPostsForUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            UserDTO user = userService.getUserByEmail(email, true);
            if(user==null) throw new UserNotFoundException("", email);
            List<Post> userPosts = user.getPosts();
            if(userPosts==null || userPosts.isEmpty()) {
                throw new PostNotFoundException("No Posts found for User", user.getFirstName());
            }
            return new ResponseEntity<>(userPosts, HttpStatus.FOUND);
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

    @PutMapping("id/{email}/{id}")
    public ResponseEntity<?> updatePost(@RequestBody Post post, @PathVariable String id, @PathVariable String email){
        try {
            Post dbPost = postService.getById(id);
            if(dbPost==null) throw new PostNotFoundException("", email);
            System.out.println(post.toString()+" : "+EqualsBuilder.reflectionEquals(dbPost, post, "id"));
            if(EqualsBuilder.reflectionEquals(dbPost, post, "id")) {
                return new ResponseEntity<>("Post data same, no changes made", HttpStatus.NO_CONTENT);
            }
            dbPost.updateData(post);
            Post updatedPost = postService.save(dbPost);
            return new ResponseEntity<>(updatedPost,HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getClass() + ", "+ e.getMessage()  );
            String message = e instanceof UncategorizedMongoDbException ? "Database Error" : e.getLocalizedMessage();
            return new ResponseEntity<>(message,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
