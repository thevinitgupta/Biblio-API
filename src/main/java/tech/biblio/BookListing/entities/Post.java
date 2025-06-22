package tech.biblio.BookListing.entities;

import com.mongodb.lang.Nullable;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("post")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    @Id
    private ObjectId id;
    private String content;
    private int likes;
    private String[] comments;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NonNull
    @Indexed(unique = true)
    String slug;

    @DBRef
    private Book book;

    private String coverImage;

    @Nullable
    private PostVectorStatus vectorStatus;

    @Nullable
    private int retryCount;

    @Nullable
    private String vectorError;

    public void updateData(Post newPost) {

        BeanUtils.copyProperties(newPost, this, "id");
        this.updatedAt = LocalDateTime.now();
    }
}
