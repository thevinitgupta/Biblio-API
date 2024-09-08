package tech.biblio.BookListing.exceptions;

public class UserNotFoundException extends RuntimeException {
    private String username;
    public UserNotFoundException(String message, String username){
        super(message);
        this.username = username;
    }

    public String getMessage(){
        return "No Users found with Email : "+this.username;
    }
}
