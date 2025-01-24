package tech.biblio.BookListing.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class BookIndustryIdentifier {
    private String type;
    private String identifier;
}
