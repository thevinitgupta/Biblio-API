package tech.biblio.BookListing.dto;

import java.util.List;

public record FetchCommentsResponseDTO(
        String message,
        List<CommentDTO> comments,
        String status,
        PaginationDTO pagination) {
}
