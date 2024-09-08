package tech.biblio.BookListing.handlers;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.dto.ErrorResponse;

@Component
@NoArgsConstructor
public class InvalidValueExceptionHandler {
    public ErrorResponse handler(Exception ex){
        if(ex instanceof StringIndexOutOfBoundsException){
            return ErrorResponse.builder()
                    .error("String Value Error")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription("Error parsing String value")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
        else if(ex instanceof ArrayIndexOutOfBoundsException){
            return ErrorResponse.builder()
                    .error("List Value Error")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription("Check size of List passed")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
        else if(ex instanceof NumberFormatException){
            return ErrorResponse.builder()
                    .error("Number Error")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription("Check value of Number passed")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
        else if(ex instanceof NullPointerException){
            return ErrorResponse.builder()
                    .error("Value Missing")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription("Required Value missing")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
        else return null;
    }
}
