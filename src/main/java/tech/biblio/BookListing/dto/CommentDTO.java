package tech.biblio.BookListing.dto;

import com.mongodb.lang.Nullable;
import lombok.Data;
import tech.biblio.BookListing.entities.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentDTO {
    private String id;
    private String postId;
    private String content;
    private String authorId;
    private String authorName; // Will be populated from User service

    @Nullable
    private String parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CommentReactionDTO commentReactions;
    private List<CommentDTO> replies = new ArrayList<>();

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.postId = comment.getPostId();
        this.content = comment.getContent();
        this.authorId = comment.getAuthorId();
        this.parentCommentId = comment.getParentId();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.authorName = comment.getAuthorName();
    }
}
