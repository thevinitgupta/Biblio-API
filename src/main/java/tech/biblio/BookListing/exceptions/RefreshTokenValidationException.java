package tech.biblio.BookListing.exceptions;

public class RefreshTokenValidationException extends JwtValidationException{
    public RefreshTokenValidationException(String validationMessage) {
        super(validationMessage);
    }
}
