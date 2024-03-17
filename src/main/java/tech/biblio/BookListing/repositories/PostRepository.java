package tech.biblio.BookListing.repositories;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import tech.biblio.BookListing.entities.Post;

@Document(collection = "posts")
public interface PostRepository extends MongoRepository<Post, String> {
}
