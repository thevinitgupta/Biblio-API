package tech.biblio.BookListing.utils;

import org.springframework.stereotype.Component;
import tech.biblio.BookListing.entities.ReactionType;

import java.util.Arrays;
import java.util.HashMap;

@Component
public class ReactionUtil {
    public HashMap<ReactionType, Integer> initReactionsCount(){
        HashMap<ReactionType, Integer> reactionsCount = new HashMap<>();
        Arrays.stream(ReactionType.values()).forEach(reactionType -> {
            reactionsCount.put(reactionType, 0);
        });
        return reactionsCount;
    }
    public String encodeKey(String key) {
        return key.replace(".", "_");
    }

    public String decodeKey(String key) {
        return key.replace("_", ".");
    }
}
