package tech.biblio.BookListing.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@JsonIgnoreProperties
@Data
@Builder
public class BookImageLinks {

    private String smallThumbnail;
    private String thumbnail;
}
