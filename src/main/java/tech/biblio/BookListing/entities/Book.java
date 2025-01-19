package tech.biblio.BookListing.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Book {
    public Book() {

    }
    private String id;

    @JsonProperty("volumeInfo")
    private BookInfo bookInfo;
}
