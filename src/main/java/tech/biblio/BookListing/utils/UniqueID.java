package tech.biblio.BookListing.utils;

import java.util.UUID;
public class UniqueID {
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}