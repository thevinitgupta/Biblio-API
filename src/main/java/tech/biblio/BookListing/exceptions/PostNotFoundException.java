package tech.biblio.BookListing.exceptions;

public class PostNotFoundException extends  RuntimeException{
    public PostNotFoundException(String message) {
        super(message);
    }
}
