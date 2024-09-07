package tech.biblio.BookListing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    @JsonProperty("error")
    String error;

    @JsonProperty("description")
    String errorDescription;

}
