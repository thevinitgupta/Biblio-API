package tech.biblio.BookListing.services;

import io.appwrite.exceptions.AppwriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.biblio.BookListing.dto.*;
import tech.biblio.BookListing.entities.Book;
import tech.biblio.BookListing.entities.EntityType;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.exceptions.BookUploadException;
import tech.biblio.BookListing.exceptions.FileTypeNotAllowedException;
import tech.biblio.BookListing.exceptions.UserNotFoundException;
import tech.biblio.BookListing.external.queue.PostVectorQueue;
import tech.biblio.BookListing.mappers.PostMapper;
import tech.biblio.BookListing.repositories.PostRepository;
import tech.biblio.BookListing.utils.Helper;
import tech.biblio.BookListing.utils.ImageUtil;
import tech.biblio.BookListing.utils.UniqueID;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
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
    private MongoTemplate mongoTemplate;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private PostVectorQueue postVectorQueue;

    @Autowired
    private Helper helper;

    @Autowired
    private ImageUtil imageUtil;

    @Value("${APPWRITE_PROJECT_ID}")
    private String projectId;

    @Value("${APPWRITE_SECRET_KEY}")
    private String apiKey;

    @Value("${APPWRITE_POST_IMAGE_BUCKET}")
    private String postImageBucket;

    @Transactional
    public Post addPost(String email, CreatePostDTO createPostDTO) {

        if (createPostDTO.taggedBook() == null) {
            throw new MissingResourceException("Book not added for post", "Book", "TaggedBook");
        }

        Book savedBook = bookService.saveBook(createPostDTO.taggedBook());
        if (savedBook == null) {
            throw new BookUploadException("Error while saving book :" + createPostDTO.taggedBook().getBookId());
        }

        Post post = Post.builder()
                .title(createPostDTO.title())
                .content(createPostDTO.content())
                .likes(0)
                .comments(new String[]{})
                .book(savedBook)
                .coverImage(createPostDTO.coverImage())
                .slug(helper.slugify(createPostDTO.title()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserDTO user = userService.getUserByEmail(email, true);
        if (user == null) throw new UserNotFoundException("User with Email not Found", email);

        Post saved = postRepository.save(post);
        if (user.getPosts() == null) {
            user.setPosts(new ArrayList<>());
        }
        user.getPosts().add(saved);
        System.out.println(user.toString());
        userService.updateUser(user);

        // Vectorization of Post - NON BLOCKING
        postVectorQueue.addPostIdToVectorQueue(saved.getSlug());

        return saved;
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    // DONE : Add Count of total posts and hasMore logic
    // DONE : Add Pagination
    // DONE : Add Offset and Page
    // DONE : Move Post DTO Mapping from Controller to Here
    // DONE : Create fetchPostsDTO and Add hasMore variable, total and current Page
    // TODO : Sorting not working
    public FetchPostsDTO getAll(int page, int offset) {

        int adjustedPage = page > 0 ? page - 1 : 0;
        Pageable paginateAndSortByUpdatedDesc = PageRequest.of(adjustedPage, offset, Sort.by("updatedAt").descending());

        Page<Post> posts = postRepository.findAll(paginateAndSortByUpdatedDesc);

        long totalCount = mongoTemplate.count(
                new Query(), Post.class
        );

        long currCount = ((long) page * offset) + posts.getSize();

        List<PostDTO> userPostsDTO = posts.stream()
                .map(post -> {
                    post.setLikes((int) reactionService.countTotalReactions(EntityType.POST, post.getId().toString()));
                    return post;
                }).map((PostMapper::postDTO))
                .toList();


        return new FetchPostsDTO(userPostsDTO,
                new PaginationDTO(
                        adjustedPage,
                        totalCount,
                        (int) Math.ceil((double) totalCount / offset),
                        currCount < totalCount));
    }

    public Post getById(String id) {
        Query query = new Query(
                new Criteria().orOperator(
                        Criteria.where("_id").is(id),
                        Criteria.where("slug").is(id)
                )
        );
        return mongoTemplate.findOne(query, Post.class);
//        return postRepository.findById(postId).orElse(null);
    }


    public String uploadPostImage(MultipartFile multipartFile, String email) throws
            FileNotFoundException, FileTypeNotAllowedException, AppwriteException, IOException, ExecutionException, InterruptedException {
        File file = null;
        file = imageUtil.convertToFile(multipartFile);
        if (file.length() > 5 * 1024 * 1024) {
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
    public byte[] getPostImage(String imageId) throws AppwriteException, ExecutionException, InterruptedException {

        return imageUtil.getImagePreview(projectId, postImageBucket, apiKey, imageId);

    }
    // TODO : Delete Post Image

}
