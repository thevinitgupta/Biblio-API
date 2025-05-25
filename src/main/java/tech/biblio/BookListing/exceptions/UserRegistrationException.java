package tech.biblio.BookListing.exceptions;

import tech.biblio.BookListing.entities.User;

public class UserRegistrationException extends RuntimeException {
    User user = null;

    public UserRegistrationException(User savedUser, String message) {
        super(message);
        user = savedUser;
    }
}
