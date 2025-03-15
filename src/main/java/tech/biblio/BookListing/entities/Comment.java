package tech.biblio.BookListing.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "comment")
@NoArgsConstructor
public class Comment {
    @MongoId
    private String id;

    private String authorId;
    private String authorName;

    @Indexed
    private String postId;
    private String content;

    @Indexed
    private String parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @DBRef
    private List<Reaction> reactions = new ArrayList<>();

    public Comment(String authorId, String postId, String content, String parentId) {
        this.authorId = authorId;
        this.content = content;
        this.postId = postId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.parentId = parentId;
    }
}
