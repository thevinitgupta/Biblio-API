package tech.biblio.BookListing.exceptions;

public class AccessTokenValidationException extends JwtValidationException{
    public AccessTokenValidationException(String validationMessage) {
        super(validationMessage);
    }
}
