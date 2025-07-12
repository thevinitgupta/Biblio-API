package tech.biblio.BookListing.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tech.biblio.BookListing.dto.PostTitleOnly;
import tech.biblio.BookListing.entities.Post;

import java.util.Optional;

@Document(collection = "post")
public interface PostRepository
        extends MongoRepository<Post, ObjectId> {

    @Query(value = "{ 'slug' : ?0 }",
            fields = "{" +
                    "'title' : 1"+
                    "}"
    )
    public Optional<PostTitleOnly> getTitleBySlug(String slug);
}
