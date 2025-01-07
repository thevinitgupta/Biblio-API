package tech.biblio.BookListing.handlers;

import io.appwrite.exceptions.AppwriteException;
import org.springframework.http.HttpStatus;
import tech.biblio.BookListing.dto.ErrorResponse;

public class ServicesExceptionalHandler {
    public ErrorResponse handler(Exception ex) {
        if (ex instanceof AppwriteException) {
            return ErrorResponse.builder()
                    .error("AppwriteException")
                    .status(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                    .errorDescription("Error in external services. Please try later.")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        } else return null;
    }
}
