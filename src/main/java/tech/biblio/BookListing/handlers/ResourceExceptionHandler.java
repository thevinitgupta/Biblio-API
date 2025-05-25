package tech.biblio.BookListing.handlers;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.dto.ErrorResponse;
import tech.biblio.BookListing.exceptions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.MissingResourceException;

@NoArgsConstructor
@Component
public class ResourceExceptionHandler {
    public ErrorResponse handler(Exception e) {
        if (e instanceof UserNotFoundException) {
            return ErrorResponse.builder()
                    .error("UserNotFoundException")
                    .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .errorDescription("User with the specified email does not exist.")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        } else if (e instanceof PostNotFoundException) {
            return ErrorResponse.builder()
                    .error("PostNotFoundException")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription("The Post you are looking for does not exist")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (e instanceof FileNotFoundException) {
            return ErrorResponse.builder()
                    .error("FileNotFoundException")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .errorDescription("The Post you are looking for does not exist")
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        } else if (e instanceof CommentNotFoundException) {
            return ErrorResponse.builder()
                    .error("CommentNotFoundException")
                    .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .errorDescription("Comment with the specified ID does not exist.")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        } else if (e instanceof DbResourceModificationException) {
            return ErrorResponse.builder()
                    .error("DbResourceModificationException")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .errorDescription(e.getMessage())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        } else if (e instanceof MissingResourceException) {
            return ErrorResponse.builder()
                    .error("MissingResourceException")
                    .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .errorDescription("The requested data is not available").build();
        } else if (e instanceof FileUploadException) {
            return ErrorResponse.builder()
                    .error("FileUploadException")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .errorDescription("Error Uploading File to storage").build();
        }
        if (e instanceof BookUploadException) {
            return ErrorResponse.builder()
                    .error("BookUploadException")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .errorDescription("Book upload failed. Please try again.")
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        } else if (e instanceof IOException) {
            return ErrorResponse.builder()
                    .error("IOException")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorDescription("Error Uploading File to storage").build();
        } else return null;
    }
}
