package tech.biblio.BookListing.exceptions;

public class JwtValidationException extends RuntimeException{
    public JwtValidationException(String validationMessage){
        super(validationMessage);
    }
}
