package tech.biblio.BookListing.exceptions;

public class InvalidUserDetailsException extends RuntimeException{
    public InvalidUserDetailsException(String validationMessage){
        super(validationMessage);
    }

}
