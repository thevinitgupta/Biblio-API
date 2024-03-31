package tech.biblio.BookListing.services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.repositories.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public void addPost(Post post){
        postRepository.save(post);
    }

    public  List<Post> getAll(){
        return postRepository.findAll();
    }
}
