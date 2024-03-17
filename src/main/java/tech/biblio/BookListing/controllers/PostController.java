package tech.biblio.BookListing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.services.PostService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private PostService postService;
    @PostMapping(path = "/add")
    public void createPost(@RequestBody Post post){
        postService.addPost(post);
    }
}
