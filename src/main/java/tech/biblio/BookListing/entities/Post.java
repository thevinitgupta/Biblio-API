package tech.biblio.BookListing.entities;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("post")
@Data
@Builder
public class Post {
    @Id
    private ObjectId id;
    private String content;
    private int likes;
    private String[] comments;
    private String title;

    public void updateData(Post newPost) {
        BeanUtils.copyProperties(newPost, this, "id");
    }
}
