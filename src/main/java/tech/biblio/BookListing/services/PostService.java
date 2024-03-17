package tech.biblio.BookListing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.repositories.PostRepository;

@Service
public class PostService {
    private PostRepository postRepository;

    public void addPost(Post post){
        postRepository.save(post);
    }
}
