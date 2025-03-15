package tech.biblio.BookListing.exceptions;

import java.util.MissingResourceException;

public class CommentNotFoundException extends MissingResourceException {
    public CommentNotFoundException(String message, String className, String key) {

        super(message,className,key);
    }
}
