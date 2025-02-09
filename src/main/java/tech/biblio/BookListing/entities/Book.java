package tech.biblio.BookListing.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("book")
@Builder
public class Book {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String bookId;

    @NonNull
    private BookInfo bookInfo;
}
