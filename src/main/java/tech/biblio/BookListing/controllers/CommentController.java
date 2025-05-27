package tech.biblio.BookListing.controllers;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.biblio.BookListing.dto.CommentDTO;
import tech.biblio.BookListing.dto.CreateCommentRequestDTO;
import tech.biblio.BookListing.dto.FetchCommentsResponseDTO;
import tech.biblio.BookListing.dto.ResponseDTO;
import tech.biblio.BookListing.services.CommentService;
import tech.biblio.BookListing.utils.Helper;

@RestController
@RequestMapping("comment")
public class CommentController {

    @Autowired
    Helper helper;

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> postComment(@RequestBody CreateCommentRequestDTO createCommentDTO) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (helper.isNullOrEmpty(createCommentDTO.content()) ||
                helper.isNullOrEmpty(createCommentDTO.postId())) {
            throw new BadRequestException("Comment body and Post ID mandatory");
        }

        if (createCommentDTO.content().length() > 750) {
            throw new BadRequestException("Comment cannot be more than 750 characters");
        }

        CommentDTO commentDTO = commentService.createComment(username, createCommentDTO);

        return new ResponseEntity<>(commentDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<FetchCommentsResponseDTO> fetchPaginatedCommentsForPost(@PathVariable String postId,
                                                                                  @RequestParam(required = false, defaultValue = "1") int page,
                                                                                  @RequestParam(required = false, defaultValue = "10") int results) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (helper.isNullOrEmpty(username)) {
            username = "";
        }

        System.out.println(postId + ", " + page + ", " + results);
        if (helper.isNullOrEmpty(postId)) {
            throw new BadRequestException("Cannot fetch comments for null Post ID");
        }
        return new ResponseEntity<>(
                commentService.fetchCommentsForPost(postId, page, results, username),
                HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (helper.isNullOrEmpty(commentId)) {
            throw new BadRequestException("Comment ID cannot be null/empty");
        }

        commentService.deleteComment(commentId, username);
        return new ResponseEntity<>(new ResponseDTO(
                HttpStatus.OK.getReasonPhrase(),
                "Comment Deleted"
        ), HttpStatus.OK);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable String commentId, @RequestBody CreateCommentRequestDTO commentRequestDTO) throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();


        if (helper.isNullOrEmpty(commentId)) {
            throw new BadRequestException("Comment ID cannot be null/empty");
        }

        commentService.updateComment(commentId, username, commentRequestDTO);
        return new ResponseEntity<>(new ResponseDTO(
                HttpStatus.OK.getReasonPhrase(),
                "Comment Updated"
        ), HttpStatus.OK);
    }

}
