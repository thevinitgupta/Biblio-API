package tech.biblio.BookListing.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import tech.biblio.BookListing.entities.Post;

@Document(collection = "post")
public interface PostRepository
        extends MongoRepository<Post, ObjectId> {
}
