package tech.biblio.BookListing.exceptions;

public class EnvironmentVariableNotDefined extends RuntimeException {
    public EnvironmentVariableNotDefined(String message) {
        super(message);
    }
}
