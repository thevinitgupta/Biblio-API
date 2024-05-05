package tech.biblio.BookListing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import tech.biblio.BookListing.entities.Post;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {
    @NonNull
    private String email;
    @NonNull
    private String firstName;

    private String lastName;

    private List<Post> posts;
}
