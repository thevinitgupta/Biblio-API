package tech.biblio.BookListing.dto;

import tech.biblio.BookListing.entities.ReactionType;

import java.util.Map;
import java.util.Set;

public record ReactionsDTO(
        Set<ReactionType> userReactions,
        long totalReactions,
        Map<ReactionType, Integer> reactionsMap) {
}
