package tech.biblio.BookListing.repositories;

import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import tech.biblio.BookListing.entities.RefreshTokenStore;

@Document(collection = "refresh_token")
public interface RefreshTokenRepository extends MongoRepository<RefreshTokenStore, ObjectId> {

    public RefreshTokenStore findFirstByTokenId(@NonNull String tokenId);
}
