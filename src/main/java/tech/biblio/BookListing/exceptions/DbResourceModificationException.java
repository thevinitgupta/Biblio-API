package tech.biblio.BookListing.exceptions;

public class DbResourceModificationException extends RuntimeException {
    public DbResourceModificationException(String message) {
        super(message);
    }
}
