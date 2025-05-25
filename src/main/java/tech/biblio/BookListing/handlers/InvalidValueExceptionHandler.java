package tech.biblio.BookListing.handlers;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingRequestValueException;
import tech.biblio.BookListing.dto.ErrorResponse;
import tech.biblio.BookListing.exceptions.FileTypeNotAllowedException;
import tech.biblio.BookListing.exceptions.InvalidUserDetailsException;

@Component
@NoArgsConstructor
public class InvalidValueExceptionHandler {
    public ErrorResponse handler(Exception ex) {
        if (ex instanceof StringIndexOutOfBoundsException) {
            return ErrorResponse.builder()
                    .error("InvalidStringException")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription("Error parsing String value")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (ex instanceof ArrayIndexOutOfBoundsException) {
            return ErrorResponse.builder()
                    .error("InvalidListException")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription("Check size of List passed")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (ex instanceof NumberFormatException) {
            return ErrorResponse.builder()
                    .error("NumberFormatException")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription("Check value of Number passed")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (ex instanceof FileTypeNotAllowedException) {
            return ErrorResponse.builder()
                    .error("FileTypeNotAllowedException")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription(ex.getMessage())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (ex instanceof MissingRequestValueException) {
            return ErrorResponse.builder()
                    .error("MissingRequestValueException")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription(ex.getMessage())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (ex instanceof NullPointerException) {
            System.out.println(ex);
            return ErrorResponse.builder()
                    .error("NullPointerException")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription("Required Value missing")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (ex instanceof InvalidUserDetailsException) {
            return ErrorResponse.builder()
                    .error("AuthenticationException")
                    .status(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .errorDescription("Check Password Again")
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .build();
        } else return null;
    }
}
