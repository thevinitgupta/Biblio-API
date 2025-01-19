package tech.biblio.BookListing.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookInfo {
    public BookInfo(){

    }
    private String title;
//    private String subtitle;
    private List<String> authors;

    @JsonProperty("publishedDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    private String publishedDate;

    @JsonProperty("imageLinks")
    private BookImageLinks bookImageLinks;

    @JsonProperty("industryIdentifiers")
    private List<BookIndustryIdentifier> industryIdentifiers;
}
