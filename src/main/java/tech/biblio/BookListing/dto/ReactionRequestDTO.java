package tech.biblio.BookListing.dto;

import tech.biblio.BookListing.entities.EntityType;
import tech.biblio.BookListing.entities.ReactionType;

public record ReactionRequestDTO(String entityId,
                                 EntityType entityType,
                                 ReactionType reactionType) {
}
