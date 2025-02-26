package tech.biblio.BookListing.services;

import io.appwrite.exceptions.AppwriteException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.biblio.BookListing.dto.CreatePostDTO;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.Book;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.exceptions.BookUploadException;
import tech.biblio.BookListing.exceptions.FileTypeNotAllowedException;
import tech.biblio.BookListing.exceptions.UserNotFoundException;
import tech.biblio.BookListing.repositories.PostRepository;
import tech.biblio.BookListing.utils.ImageUtil;
import tech.biblio.BookListing.utils.UniqueID;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ImageUtil imageUtil;

    @Value("${APPWRITE_PROJECT_ID}")
    private String projectId;

    @Value("${APPWRITE_SECRET_KEY}")
    private String apiKey;

    @Value("${APPWRITE_POST_IMAGE_BUCKET}")
    private String postImageBucket;

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
                .coverImage(createPostDTO.coverImage())
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

        // TODO : Generate reaction document for post

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


    public String uploadPostImage(MultipartFile multipartFile, String email) throws
            FileNotFoundException, FileTypeNotAllowedException, AppwriteException, IOException, ExecutionException, InterruptedException {
        File file = null;
        file = imageUtil.convertToFile(multipartFile);
        if(file.length() > 5*1024*1024) {
            throw new FileTypeNotAllowedException("Max file size 5MB allowed", file.length() / 1024 + "mb", "5MB");
        }
        System.out.println("Project ID : " + projectId);
        System.out.println("postImageBucket : " + postImageBucket);
        System.out.println("apiKey : " + apiKey);
        System.out.println("File before compression : " + file);
        String extension = imageUtil.getExtension(file.getName());
        if (!List.of(".png", ".jpg", ".jpeg").contains(extension)) {
            throw new FileTypeNotAllowedException("Only images of max size 5mb allowed in Profile Image",
                    extension,
                    "jpg/png/jpeg");
        }

        String postImageId = UniqueID.generateLongId();
        file = imageUtil.compressPostImage(file, postImageId + "_post_img" + extension);
        // check if file exists
        System.out.println("File before compression : " + file);

        // create new file

        io.appwrite.models.File uploadedFile = imageUtil.uploadPostImage(projectId, postImageBucket, apiKey, file, postImageId);

        return uploadedFile != null ? postImageId : "";
    }

    // TODO : Get Post Image
    public byte [] getPostImage(String imageId) throws AppwriteException, ExecutionException, InterruptedException {

        return imageUtil.getImagePreview(projectId, postImageBucket, apiKey, imageId);

    }
    // TODO : Delete Post Image

}
