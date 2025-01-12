package tech.biblio.BookListing.mappers;

import tech.biblio.BookListing.dto.PostDTO;
import tech.biblio.BookListing.entities.Post;


public class PostMapper {
    public static PostDTO postDTO(Post post){
        return PostDTO.builder()
                .id(post.getId().toHexString())
                .title(post.getTitle())
                .content(post.getContent())
                .comments(post.getComments())
                .likes(post.getLikes())
                .build();
    }
}
