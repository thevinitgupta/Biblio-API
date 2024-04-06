package tech.biblio.BookListing.entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Data
public class Comment {
    @MongoId
    private ObjectId userId;
    private String username;
    private String content;
    private Date createdAt;
    private Date updatedAt;

    public Comment(ObjectId userId, String username, String content, Date createdAt) {
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }
}
