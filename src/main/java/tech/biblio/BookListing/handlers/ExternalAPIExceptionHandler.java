package tech.biblio.BookListing.handlers;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.dto.ErrorResponse;
import tech.biblio.BookListing.exceptions.SimilaritySearchException;

@NoArgsConstructor
@Component
public class ExternalAPIExceptionHandler {
    public ErrorResponse handler(Exception e) {
        if (e instanceof SimilaritySearchException) {
            return ErrorResponse.builder()
                    .error("SimilaritySearchException")
                    .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .errorDescription(e.getLocalizedMessage())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }else return null;
    }
}
