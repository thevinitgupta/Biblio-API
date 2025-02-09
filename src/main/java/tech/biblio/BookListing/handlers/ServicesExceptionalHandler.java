package tech.biblio.BookListing.handlers;

import io.appwrite.exceptions.AppwriteException;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.dto.ErrorResponse;
import tech.biblio.BookListing.exceptions.GoogleApiBooksException;
import tech.biblio.BookListing.exceptions.RateLimitExceededException;

@NoArgsConstructor
@Component
public class ServicesExceptionalHandler {
    public ErrorResponse handler(Exception ex) {
        if (ex instanceof RateLimitExceededException) {
            return ErrorResponse.builder()
                    .error("RateLimitExceededException")
                    .status(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase())
                    .errorDescription(ex.getMessage())
                    .httpStatus(HttpStatus.TOO_MANY_REQUESTS)
                    .build();
        }
        else if (ex instanceof AppwriteException) {
            return ErrorResponse.builder()
                    .error("AppwriteException")
                    .status(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                    .errorDescription("Error in external services. Please try later.")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (ex instanceof GoogleApiBooksException) {
            return ErrorResponse.builder()
                    .error("GoogleApiBooksException")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .errorDescription(ex.getMessage().isEmpty() ? "Error while getting books data" : ex.getMessage())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        } else return null;
    }
}
