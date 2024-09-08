package tech.biblio.BookListing.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    @JsonProperty("error")
    String error;

    @JsonProperty("description")
    String errorDescription;


    @JsonProperty("status")
    String status;

    @JsonIgnore
    HttpStatus httpStatus;
}
