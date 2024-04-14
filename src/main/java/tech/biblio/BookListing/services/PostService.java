package tech.biblio.BookListing.services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.entities.User;
import tech.biblio.BookListing.exceptions.UserNotFoundException;
import tech.biblio.BookListing.repositories.PostRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;

    public Post addPost(String email, Post post){
        User user = userService.getUserByEmail(email);
        if(user==null) throw new UserNotFoundException("User with Email not Found");
        System.out.println(user);
        Post saved = postRepository.save(post);
        user.getPosts().add(saved);
        userService.addUser(user);
        return saved;
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }
    public  List<Post> getAll(){
        return postRepository.findAll();
    }

    public Post getById(String id) {
        ObjectId postId = new ObjectId(id);
        return postRepository.findById(postId).orElse(null);
    }
}
