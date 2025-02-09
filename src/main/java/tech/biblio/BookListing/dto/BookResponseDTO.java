package tech.biblio.BookListing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import tech.biblio.BookListing.entities.Book;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BookResponseDTO {
    public BookResponseDTO() {

    }
    @JsonProperty("items")
    private List<Book> items;
}
