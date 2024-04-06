package tech.biblio.BookListing.controllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.services.PostService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("posts")
public class PostController {
    @Autowired
    private PostService postService;

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
    @PostMapping()
    public ResponseEntity<?> createPost(@RequestBody Post post){
        System.out.println(post.toString());
        try {
            Post savedPost = postService.addPost(post);
            return new ResponseEntity<Post>(savedPost, HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println(e.getClass() + ", "+ e.getMessage()  );
            String message = e instanceof UncategorizedMongoDbException ? "Database Error" : e.getLocalizedMessage();
            return new ResponseEntity<>(message,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
