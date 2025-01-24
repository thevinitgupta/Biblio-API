package tech.biblio.BookListing.services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.biblio.BookListing.dto.CreatePostDTO;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.Book;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.exceptions.BookUploadException;
import tech.biblio.BookListing.exceptions.UserNotFoundException;
import tech.biblio.BookListing.repositories.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Transactional
    public Post addPost(String email, CreatePostDTO createPostDTO){

        if(createPostDTO.taggedBook()==null){
            throw new MissingResourceException("Book not added for post", "Book", "TaggedBook");
        }

        Book savedBook = bookService.saveBook(createPostDTO.taggedBook());
        if(savedBook==null) {
            throw new BookUploadException("Error while saving book :"+createPostDTO.taggedBook().getBookId());
        }

        Post post = Post.builder()
                .title(createPostDTO.title())
                .content(createPostDTO.content())
                .likes(0)
                .comments(new String[]{})
                .book(savedBook)
                .build();

        UserDTO user = userService.getUserByEmail(email, true);
        if(user==null) throw new UserNotFoundException("User with Email not Found", email);

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
