package tech.biblio.BookListing.dto;

public record CreateCommentRequestDTO(String content, String postId, String parentCommentId) {
}
