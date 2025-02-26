package tech.biblio.BookListing.utils;

import org.springframework.stereotype.Component;

@Component
public class Helper {
    public boolean isNullOrEmpty(String strToCheck){
        return strToCheck == null || strToCheck.isBlank();
    }
}
