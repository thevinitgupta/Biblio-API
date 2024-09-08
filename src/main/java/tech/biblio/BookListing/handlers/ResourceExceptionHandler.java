package tech.biblio.BookListing.handlers;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.dto.ErrorResponse;
import tech.biblio.BookListing.exceptions.PostNotFoundException;
import tech.biblio.BookListing.exceptions.UserNotFoundException;

import java.util.MissingResourceException;

@NoArgsConstructor
@Component
public class ResourceExceptionHandler {
    public ErrorResponse handler(Exception e){
        if(e instanceof UserNotFoundException){
            return ErrorResponse.builder()
                    .error("User Not Found")
                    .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .errorDescription("User with the specified email does not exist.")
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }
        else if(e instanceof PostNotFoundException){
            return ErrorResponse.builder()
                    .error("Post Not Found")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription("The Post you are looking for does not exist")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
        else if(e instanceof MissingResourceException){
            return ErrorResponse.builder()
                    .error("Missing Resource")
                    .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .errorDescription("The requested data is not available").build();
        }
        else return null;
    }
}
