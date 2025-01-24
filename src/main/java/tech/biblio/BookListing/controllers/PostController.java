package tech.biblio.BookListing.controllers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.biblio.BookListing.dto.CreatePostDTO;
import tech.biblio.BookListing.dto.FetchPostDTO;
import tech.biblio.BookListing.dto.PostDTO;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.exceptions.PostNotFoundException;
import tech.biblio.BookListing.exceptions.UserNotFoundException;
import tech.biblio.BookListing.mappers.PostMapper;
import tech.biblio.BookListing.services.BookService;
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

    @Autowired
    private BookService bookService;

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

        Post savedPost = postService.addPost(email,createPostDTO);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<?> getPostsForUser(@RequestParam(required = false) String postId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Validate user existence
        UserDTO user = userService.getUserByEmail(email, true);
        if (user == null) {
            throw new UserNotFoundException("User not found", email);
        }

        // If postId is provided, fetch a single post
        if (postId != null) {
            // Validate postId
            if (!ObjectId.isValid(postId)) {
                throw new PostNotFoundException("Invalid post id, please check again", email);
            }

            Post userPost = postService.getById(postId);
            if (userPost == null) {
                throw new PostNotFoundException("The Post you are requesting does not exist", email);
            }

            return new ResponseEntity<>(new FetchPostDTO(
                    HttpStatus.OK.getReasonPhrase(),
                    PostMapper.postDTO(userPost)
            ), HttpStatus.OK);
        }

        List<Post> userPosts = user.getPosts();
        if (userPosts == null || userPosts.isEmpty()) {
            throw new PostNotFoundException("No Posts found for User", user.getFirstName());
        }

        List<PostDTO> userPostsDTO = userPosts.stream()
                .map((PostMapper::postDTO)).toList();

        return new ResponseEntity<>(userPostsDTO, HttpStatus.OK);
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
/*
    @GetMapping
    public ResponseEntity<?> getPostById(@RequestParam String postId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        boolean isValidUser = userService.checkUserExists(email);
        if(!isValidUser) throw new UserNotFoundException("", email);

        if(postId==null || !ObjectId.isValid(postId)){
            throw new PostNotFoundException("Invalid post id, please check again", email);
        }
        Post userPost = null;
        userPost = postService.getById(postId);

        if(userPost==null){
            throw new PostNotFoundException("The Post you are requesting does not exist", email);
        }
        return new ResponseEntity<>(new FetchPostDTO(
                HttpStatus.OK.getReasonPhrase(),
                userPost
        ), HttpStatus.OK);
    }
    */

}
