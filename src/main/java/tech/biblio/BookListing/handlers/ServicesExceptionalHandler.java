package tech.biblio.BookListing.handlers;

import io.appwrite.exceptions.AppwriteException;
import org.springframework.http.HttpStatus;
import tech.biblio.BookListing.dto.ErrorResponse;
import tech.biblio.BookListing.exceptions.GoogleApiBooksException;

public class ServicesExceptionalHandler {
    public ErrorResponse handler(Exception ex) {
        if (ex instanceof AppwriteException) {
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
