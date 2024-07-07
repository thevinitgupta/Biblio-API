package tech.biblio.BookListing.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import tech.biblio.BookListing.entities.Role;

public interface RoleRepository extends MongoRepository<Role, ObjectId> {
    Role findByName(String name);
}
