package tech.biblio.BookListing.services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.exceptions.UserNotFoundException;
import tech.biblio.BookListing.repositories.PostRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public Post addPost(String email, Post post){
        UserDTO user = userService.getUserByEmail(email, true);
        if(user==null) throw new UserNotFoundException("User with Email not Found", email);
        System.out.println(user);
        Post saved = postRepository.save(post);
        if(user.getPosts()==null){
            user.setPosts(new ArrayList<>( ));
        }
        user.getPosts().add(saved);
        System.out.println(user.toString());
        userService.updateUser(user);
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
