package tech.biblio.BookListing.dto;

import lombok.Builder;

@Builder
public record ReactionsResponseDTO(String message, ReactionsDTO reactions) {
}
