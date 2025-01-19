package tech.biblio.BookListing.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties
@Data
public class BookImageLinks {
    public BookImageLinks(){

    }
    private String smallThumbnail;
    private String thumbnail;
}
