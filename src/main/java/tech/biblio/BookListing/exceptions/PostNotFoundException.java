package tech.biblio.BookListing.exceptions;

public class PostNotFoundException extends  RuntimeException {
    private String username;
    public PostNotFoundException(String message, String username) {
        super(message);
        this.username = username;
    }
    public String getMessage(){
        return "No Posts found for "+username;
    }
}
