package tech.biblio.BookListing.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "refresh_token")
@Data
@Builder
public class RefreshTokenStore {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NonNull
    private String tokenId;

    @NonNull
    private String refreshToken;

    @NonNull
    private String username; // owner id

    @NonNull
    @CreatedDate
    private Date issuedAt;

    @NonNull
    private Boolean isValid;

}
