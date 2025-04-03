package tech.biblio.BookListing.dto;

import java.util.List;

public record FetchPostsDTO(List<PostDTO> posts, PaginationDTO pagination) {
}
