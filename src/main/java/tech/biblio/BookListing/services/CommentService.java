package tech.biblio.BookListing.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.dto.*;
import tech.biblio.BookListing.entities.Comment;
import tech.biblio.BookListing.entities.EntityType;
import tech.biblio.BookListing.exceptions.CommentNotFoundException;
import tech.biblio.BookListing.exceptions.DbResourceModificationException;
import tech.biblio.BookListing.repositories.CommentRepository;
import tech.biblio.BookListing.utils.Helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    Helper helper;



    public CommentDTO createComment(String authorId, CreateCommentRequestDTO createCommentDTO) throws BadRequestException {
        Comment newComment = new Comment(
                authorId,
                createCommentDTO.postId(),
                createCommentDTO.content(),
                createCommentDTO.parentCommentId()
        );

        if(helper.isNullOrEmpty(newComment.getParentId()) || newComment.getParentId().equals("null")){
            newComment.setParentId(null);
        }

        Optional<Comment> parentComment;
        if(!helper.isNullOrEmpty(newComment.getParentId())){
            parentComment = commentRepository.findById(new ObjectId(newComment.getParentId()));
            if(parentComment.isEmpty()){
               throw new BadRequestException("Invalid Parent Comment provided");
            }
        }

        UserDTO commentUser = userService.getUserByEmail(authorId,false);
        newComment.setAuthorName(commentUser.getFirstName()+" "+commentUser.getLastName());

        Comment savedComment = commentRepository.save(newComment);
        return new CommentDTO(savedComment);
    }


    // Done : DOes not work - not even just with postid
    // Works with Mongo template and parentID
    public FetchCommentsResponseDTO fetchCommentsForPost(String postId, int page, int resultsPerPage, String userId){
//        List<Comment> dbComments = new ArrayList<>();
//        Pageable paginateAndSortByUpdatedDesc = PageRequest.of(page,resultsPerPage, Sort.by("updatedAt").descending());
//
//        List<Comment> baseCommentsForPost = commentRepository.findAllByPostId(
//                postId,
//                paginateAndSortByUpdatedDesc);
        int adjustedPage = page > 0 ? page - 1 : 0;
        Pageable paginateAndSortByUpdatedDesc = PageRequest.of(adjustedPage,resultsPerPage, Sort.by("updatedAt").descending());
        Query query = new Query(
                new Criteria().andOperator(
                        Criteria.where("postId").is(postId).and("parentId").isNull()
                ));
        query.with(paginateAndSortByUpdatedDesc);
        List<Comment> baseCommentsForPost = mongoTemplate.find(query, Comment.class);

        // Get total count for pagination metadata
        Query countQuery = new Query(
                new Criteria().andOperator(
                        Criteria.where("postId").is(postId).and("parentId").isNull()
                ));
        long totalItems = mongoTemplate.count(countQuery, Comment.class);


        System.out.println("Base Comments for Post : "+baseCommentsForPost);

        // DONE : Fetch child comments for each comment above
        // TODO: (can be done in a better way for more than 1 nested labels)
        HashMap<String, List<Comment>> repliesMap = new HashMap<>();
        for(Comment baseComment : baseCommentsForPost){
            String parentId = baseComment.getId();
            List<Comment> repliesForBase = commentRepository.findAllByParentIdOrderByCreatedAtAsc(
                    parentId);
            repliesMap.put(parentId, repliesForBase);
        }

        // DONE : Total Reaction count for each comment(base and nested)
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for(Comment baseComment : baseCommentsForPost){
            CommentDTO baseCommentDTO = new CommentDTO(baseComment);
            if(!userId.isEmpty()) {
                ReactionsDTO baseCommentReactions = reactionService.getReactions(
                        EntityType.COMMENT,
                        baseCommentDTO.getId(),
                        userId
                );
                baseCommentDTO.setCommentReactions(
                        new CommentReactionDTO(
                                // DONE : Flag is comment is liked current user
                                !baseCommentReactions.userReactions().isEmpty(),
                                baseCommentReactions.totalReactions()
                        )
                );
            }
            List<CommentDTO> repliesCommentDTOList = new ArrayList<>();
            for(Comment reply : repliesMap.get(baseComment.getId())){
                CommentDTO replyCommentDTO = new CommentDTO(reply);
                if(!userId.isEmpty()) {
                    ReactionsDTO replyCommentReactions = reactionService.getReactions(
                            EntityType.COMMENT,
                            replyCommentDTO.getId(),
                            userId
                    );
                    replyCommentDTO.setCommentReactions(
                            new CommentReactionDTO(
                                    // DONE : Flag is comment is liked current user
                                    !replyCommentReactions.userReactions().isEmpty(),
                                    replyCommentReactions.totalReactions()
                            )
                    );
                }
                repliesCommentDTOList.add(replyCommentDTO);
            }
            baseCommentDTO.setReplies(repliesCommentDTOList);

            commentDTOList.add(baseCommentDTO);
        }

        System.out.println("Comment DTO List : "+commentDTOList);

        PaginationDTO paginationDTO = new PaginationDTO(
                page,
                totalItems,
                (int) Math.ceil((double) totalItems / resultsPerPage),
                adjustedPage < (int) Math.ceil((double) totalItems / resultsPerPage) - 1
        );
        return new FetchCommentsResponseDTO(
                "Fetch Comments Successful",
                commentDTOList,
                HttpStatus.OK.getReasonPhrase(),
                paginationDTO
        );
    }


    public void deleteComment(String commentIdStr, String userId){
        ObjectId commentId = new ObjectId(commentIdStr);
        if(!commentRepository.existsById(commentId)){
            throw new CommentNotFoundException(
                    "Comment not found for given ID",
                    Comment.class.getName(),
                    commentIdStr);
        }
        try {
            /*
            * wrong query :
            * Query deleteQuery = new Query(Criteria.where("id").is(commentIdStr)
                .orOperator(Criteria.where("parentId").is(commentIdStr)));
            * */
            Query deleteQuery = new Query(
                    new Criteria().orOperator(
                            Criteria.where("id").is(commentIdStr),
                            Criteria.where("parentId").is(commentIdStr)
                    ).andOperator(
                            Criteria.where("authorId").is(userId)
                    )
            );
            mongoTemplate.findAllAndRemove(deleteQuery, Comment.class);

        }catch (Exception e){
            log.error("Error deleting comments by ID/Parent ID/Author ID : \n"+e.getMessage());
            throw new DbResourceModificationException("Error Deleting Comments, please try again.");
        }

    }

    public void updateComment(String commentIdStr, String userId, CreateCommentRequestDTO commentRequestDTO){
        ObjectId commentId = new ObjectId(commentIdStr);
        if(!commentRepository.existsById(commentId)){
            throw new CommentNotFoundException(
                    "Comment not found for given ID",
                    Comment.class.getName(),
                    commentIdStr);
        }
        try {
            /*
            * wrong query :
            * Query deleteQuery = new Query(Criteria.where("id").is(commentIdStr)
                .orOperator(Criteria.where("parentId").is(commentIdStr)));
            * */
            Query updateQuery = new Query(
                    new Criteria().andOperator(
                            Criteria.where("id").is(commentIdStr),
                            Criteria.where("authorId").is(userId),
                            Criteria.where("parentId").is(commentRequestDTO.parentCommentId())
                    )
            );

//            System.out.println("\n\nUpdate Query : "+updateQuery+"\n\n");
            Update updateComment = new Update().set(
                    "content",
                    commentRequestDTO.content()
            ).set(
                    "updatedAt",
                    LocalDateTime.now()
            );
//            System.out.println("\n\nUpdate Query Result : "+mongoTemplate.find(updateQuery,Comment.class)+"\n\n");
            mongoTemplate.findAndModify(updateQuery, updateComment,Comment.class);

        }catch (Exception e){
            log.error("Error updating comment by ID/Parent ID/Author ID : \n"+e.getMessage());
            throw new DbResourceModificationException("Error Updating Comments, please try again.");
        }

    }
}
