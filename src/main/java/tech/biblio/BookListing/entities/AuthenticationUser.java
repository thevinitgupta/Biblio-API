package tech.biblio.BookListing.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@NoArgsConstructor
@Document(collection = "auth_user")
public class AuthenticationUser {

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    @NonNull
    private String username;

    private String password;

    private String role;

}
