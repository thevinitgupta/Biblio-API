package tech.biblio.BookListing.utils;


import io.viascom.nanoid.NanoId;

import java.util.UUID;

public class UniqueID {
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
    public static String generateLongId(){
        return  UUID.randomUUID().toString();
    }

    public static String shortId(){
//        SecureRandom random = new SecureRandom();
//        char [] alphabets = "qwertyuiopasdfghjklzxcvbnm_QWERTYUIOPASDFGHJKLZXCVBNM1234567890"
//                .toCharArray();
        String shortId = NanoId.generate(7);
        return shortId;
    }
}