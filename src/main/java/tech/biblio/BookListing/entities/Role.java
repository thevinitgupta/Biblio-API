package tech.biblio.BookListing.entities;

import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Set;

@Data
@Document(collection = "roles")
public class Role {
    @MongoId
    public ObjectId id;

    @NonNull
    @Indexed(unique = true)
    public String name;

    public Set<Privilege> privileges;

}
