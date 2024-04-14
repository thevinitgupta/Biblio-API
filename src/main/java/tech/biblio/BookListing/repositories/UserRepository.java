package tech.biblio.BookListing.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import tech.biblio.BookListing.entities.Post;
import tech.biblio.BookListing.entities.User;

import java.util.List;

@Document(collection = "user")
public interface UserRepository
        extends MongoRepository<User, ObjectId> {
    List<User> findByFirstName(String firstName);
    List<User> findAllByFirstName(String firstName);
    User findFirstByEmail(String email);

    List<User> findByEmailAndFirstNameIsIgnoreCase(String email, String firstName);
}
