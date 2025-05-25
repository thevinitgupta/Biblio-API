package tech.biblio.BookListing.controllers;

import io.appwrite.exceptions.AppwriteException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.biblio.BookListing.annotations.RateLimited;
import tech.biblio.BookListing.dto.CreatePostDTO;
import tech.biblio.BookListing.dto.FetchPostDTO;
import tech.biblio.BookListing.dto.FetchPostsDTO;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.exceptions.FileTypeNotAllowedException;
import tech.biblio.BookListing.exceptions.PostNotFoundException;
import tech.biblio.BookListing.mappers.PostMapper;
import tech.biblio.BookListing.services.BookService;
import tech.biblio.BookListing.services.PostService;
import tech.biblio.BookListing.services.UserService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


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
    public ResponseEntity<?> createPost(@RequestBody CreatePostDTO createPostDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Post savedPost = postService.addPost(email, createPostDTO);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);

    }

    @RateLimited
    @GetMapping
    public ResponseEntity<?> getPostsForUser(@RequestParam(required = false) String postId,
                                             @RequestParam(required = false, defaultValue = "1") int page,
                                             @RequestParam(required = false, defaultValue = "10") int offset) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
//
//        // Validate user existence
//        UserDTO user = userService.getUserByEmail(email, true);
//        if (user == null) {
//            throw new UserNotFoundException("User not found", email);
//        }

        // If postId is provided, fetch a single post
        if (postId != null) {
            // Validate postId
//            if (!ObjectId.isValid(postId)) {
//                throw new PostNotFoundException("Invalid post id, please check again", email);
//            }

            Post currentPost = postService.getById(postId);
            if (currentPost == null) {
                throw new PostNotFoundException("The Post you are requesting does not exist");
            }

//            Book postBook = bookService.getBookById(userPost.getBook().getId());

            return new ResponseEntity<>(new FetchPostDTO(
                    HttpStatus.OK.getReasonPhrase(),
                    PostMapper.postDTOWithBook(currentPost)
            ), HttpStatus.OK);
        }

        FetchPostsDTO publicPosts = postService.getAll(page, offset);
        if (publicPosts == null || publicPosts.posts().isEmpty()) {
            throw new PostNotFoundException("No public Posts found");
        }


        return new ResponseEntity<>(publicPosts, HttpStatus.OK);
    }


    @PutMapping("id/{email}/{id}")
    public ResponseEntity<?> updatePost(@RequestBody Post post, @PathVariable String id, @PathVariable String email) {
        try {
            Post dbPost = postService.getById(id);
            if (dbPost == null) throw new PostNotFoundException("", email);
            System.out.println(post.toString() + " : " + EqualsBuilder.reflectionEquals(dbPost, post, "id"));
            if (EqualsBuilder.reflectionEquals(dbPost, post, "id")) {
                return new ResponseEntity<>("Post data same, no changes made", HttpStatus.NO_CONTENT);
            }
            dbPost.updateData(post);
            Post updatedPost = postService.save(dbPost);
            return new ResponseEntity<>(updatedPost, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getClass() + ", " + e.getMessage());
            String message = e instanceof UncategorizedMongoDbException ? "Database Error" : e.getLocalizedMessage();
            return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadPostImage(@RequestParam("file") MultipartFile multipartFile) throws AppwriteException, IOException, ExecutionException, InterruptedException, FileTypeNotAllowedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        String imageUploaded = postService.uploadPostImage(multipartFile, email);
        if (!imageUploaded.isEmpty()) {
            return new ResponseEntity<>("{\n" +
                    "\"postImage\" :\"" + imageUploaded + "\"" +
                    "\n}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\n" +
                    "\"postImage\" :\"Image Upload Failed\"" +
                    "\n}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RateLimited
    @GetMapping("/image/{id}")
    public ResponseEntity<?> fetchPostImage(@PathVariable String id) throws AppwriteException, IOException, ExecutionException, InterruptedException, FileTypeNotAllowedException, MissingRequestValueException {

        if (id == null || id.isEmpty()) {
            throw new MissingRequestValueException("Image ID not provided");
        }
        byte[] fileBytes = postService.getPostImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename("post_img.png").build());
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}
