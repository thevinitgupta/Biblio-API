package tech.biblio.BookListing.exceptions;

public class FileTypeNotAllowedException extends Exception{
    String allowedType;
    String currentType;
    public FileTypeNotAllowedException(String message, String currentType, String allowedType){
        super(message);
        this.allowedType = allowedType;
        this.currentType = currentType;
    }
}
