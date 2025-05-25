package tech.biblio.BookListing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.dto.ReactionRequestDTO;
import tech.biblio.BookListing.dto.ReactionsDTO;
import tech.biblio.BookListing.entities.EntityType;
import tech.biblio.BookListing.entities.Reaction;
import tech.biblio.BookListing.entities.ReactionType;
import tech.biblio.BookListing.repositories.ReactionRepository;
import tech.biblio.BookListing.utils.ReactionUtil;

import java.util.*;


@Service
public class ReactionService {
    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private ReactionUtil reactionUtil;

    public ReactionsDTO getReactions(EntityType entityType, String entityId, String userEmail) {
        String userId = reactionUtil.encodeKey(userEmail);
        // TODO : Filter based on entityType and EntityId
        // TODO : Only fetch current UserID data from DB
        Reaction userReaction = reactionRepository.findByEntityIdAndEntityTypeAggregated(entityId, entityType, reactionUtil.encodeKey(userId))
                .orElse(Reaction.builder()
                        .reactions(new HashMap<>())
                        .entityType(entityType)
                        .entityId(entityId)
                        .reactionCount(reactionUtil.initReactionsCount())
                        .lastModifiedOn(new Date())
                        .build());

        // TODO : Calculate total reactions count
        long totalReactionsCount = 0;
        for (Map.Entry<ReactionType, Integer> reactionEntry : userReaction.getReactionCount().entrySet()) {
            totalReactionsCount += reactionEntry.getValue();
        }

        return new ReactionsDTO(
                userReaction.getReactions().getOrDefault(userId, new HashSet<>()),
                totalReactionsCount,
                userReaction.getReactionCount());
    }

    public boolean toggleReaction(ReactionRequestDTO reactionRequestDTO, String userId) {
        Optional<Reaction> optionalReaction = reactionRepository
                .findByEntityIdAndEntityType(reactionRequestDTO.entityId(),
                        reactionRequestDTO.entityType());
        // counter to update total reactions count for each type
        int toggle = 0;

        Reaction reaction = optionalReaction.orElseGet(() ->
                Reaction.builder()
                        .reactionCount(reactionUtil.initReactionsCount())
                        .entityId(reactionRequestDTO.entityId())
                        .entityType(reactionRequestDTO.entityType())
                        .reactions(new HashMap<>())
                        .lastModifiedOn(new Date())
                        .build());

        // COMPLETED : Add updation of reactions (TOGGLE)

        Set<ReactionType> reactionsSet = reaction.getReactions().getOrDefault(reactionUtil.encodeKey(userId), new HashSet<>());

        if (reactionsSet.contains(reactionRequestDTO.reactionType())) {
            toggle = -1; // remove
            reactionsSet.remove(reactionRequestDTO.reactionType());
        } else {
            toggle = 1; // add
            reactionsSet.add(reactionRequestDTO.reactionType());
        }

        // Remove empty user reactions to avoid unnecessary memory usage
        if (reactionsSet.isEmpty()) {
            reaction.getReactions().remove(reactionUtil.encodeKey(userId));
        } else {
            reaction.getReactions().put(reactionUtil.encodeKey(userId), reactionsSet);
        }

        // COMPLETED : Add updation reactionsCount and last modified
        // COMPLETED : Update logic to toggle, currently only adding (BUG)
        HashMap<ReactionType, Integer> reactionsCountMap = reaction.getReactionCount();

        reactionsCountMap.put(reactionRequestDTO.reactionType(),
                Math.max(0, reactionsCountMap.getOrDefault(
                        reactionRequestDTO.reactionType(), 0) + toggle)); // add or delete based on user reaction added or removed
        reaction.setReactionCount(reactionsCountMap);

        reaction.setLastModifiedOn(new Date());

        // COMPLETED : save to DB
        reactionRepository.save(reaction);
        return true;
    }

    public long countTotalReactions(EntityType entityType, String entityId) {
        Reaction reactions = reactionRepository
                .findReactionsByEntityIdAndEntityType(entityId, entityType)
                .orElse(Reaction.builder()
                        .reactionCount(reactionUtil.initReactionsCount())
                        .entityId(entityId)
                        .entityType(entityType)
                        .reactions(new HashMap<>())
                        .lastModifiedOn(new Date())
                        .build());

        long totalReactionsCount = 0;
        for (Map.Entry<ReactionType, Integer> reactionEntry : reactions.getReactionCount().entrySet()) {
            totalReactionsCount += reactionEntry.getValue();
        }

        return totalReactionsCount;
    }
}
