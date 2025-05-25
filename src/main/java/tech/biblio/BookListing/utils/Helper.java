package tech.biblio.BookListing.utils;

import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
public class Helper {

    public boolean isNullOrEmpty(String strToCheck) {
        return strToCheck == null || strToCheck.isBlank();
    }

    public String slugify(String title) {
        return Normalizer
                .normalize(title, Normalizer.Form.NFD)
                .replaceAll("[^\\w\\s-]", "") // Remove all non-word, non-space or non-dash characters
                .replace('-', ' ') // Replace dashes with spaces
                .trim() // Trim leading/trailing whitespace (including what used to be leading/trailing dashes)
                .replaceAll("\\s+", "-") // Replace whitespace (including newlines and repetitions) with single dashes
                .toLowerCase() // Lowercase the final results
                .concat("-") // separate title from ShortId
                .concat(
                        UniqueID.shortId()
                );  // add Short ID to slug
    }
}
