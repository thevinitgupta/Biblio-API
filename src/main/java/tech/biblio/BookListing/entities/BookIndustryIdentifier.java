package tech.biblio.BookListing.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookIndustryIdentifier {
    public BookIndustryIdentifier(){

    }
    private String type;
    private String identifier;
}
