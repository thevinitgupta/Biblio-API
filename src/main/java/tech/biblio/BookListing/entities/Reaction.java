package tech.biblio.BookListing.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

@Document(collection = "reaction")
@Data
@Builder
@CompoundIndex(name = "entity_idx", def = "{'entityId': 1, 'entityType': 1}")
public class Reaction {
    @Id
    private String id;

    private String entityId; // ID of the post, comment or any other entity
    private EntityType entityType; // type of the entity

    private HashMap<String, Set<ReactionType>> reactions;

    private Date lastModifiedOn;

    private HashMap<ReactionType, Integer> reactionCount;

}
