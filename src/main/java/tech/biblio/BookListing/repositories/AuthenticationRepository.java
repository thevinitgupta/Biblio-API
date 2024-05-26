package tech.biblio.BookListing.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import tech.biblio.BookListing.entities.AuthenticationUser;

import java.util.List;

public interface AuthenticationRepository
        extends MongoRepository<AuthenticationUser, ObjectId> {
    List<AuthenticationUser> findByUsername(String username);
}
