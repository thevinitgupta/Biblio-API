package tech.biblio.BookListing.controllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Post> getAll(){
    return postService.getAll();
    }
    @PostMapping()
    public void createPost(@RequestBody Post post){
        System.out.println(post.toString());
        postService.addPost(post);
    }


}
