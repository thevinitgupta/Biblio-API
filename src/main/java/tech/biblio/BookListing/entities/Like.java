package tech.biblio.BookListing.entities;

import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

public class Like {
    @MongoId
    private String userId;
    private Date createdAt;
}
