package tech.biblio.BookListing.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tech.biblio.BookListing.entities.EntityType;
import tech.biblio.BookListing.entities.Reaction;

import java.util.Optional;

@Document(collection = "reaction")
public interface ReactionRepository extends MongoRepository<Reaction, ObjectId> {

    Optional<Reaction> findByEntityIdAndEntityType(String entityId, EntityType entityType);

    // Alternative method using aggregation pipeline for more complex scenarios
    /*
    Performance Overhead for simple queries that do not require modification
    @Aggregation(pipeline = {
            "{ $match: { 'entityId': ?0, 'entityType': ?1 } }",
            "{ $project: { " +
                    "'id': 1, " +
                    "'entityId': 1, " +
                    "'entityType': 1, " +
                    "'reactions': { $ifNull: [{ $getField: { field: ?2, input: '$reactions' } }, []] }, " +
                    "'lastModifiedOn': 1, " +
                    "'reactionCount': 1 " +
                    "} }"
    })

     */
    @Query(value = "{ 'entityId': ?0, 'entityType': ?1 }",
            fields = "{ " +
                    "'id': 1, " +
                    "'entityId': 1, " +
                    "'entityType': 1, " +
                    "'reactions': { ?2: 1 }, " +
                    "'lastModifiedOn': 1, " +
                    "'reactionCount': 1 }")
    Optional<Reaction> findByEntityIdAndEntityTypeAggregated(String entityId,
                                                                       EntityType entityType,
                                                                       String userId);
}
