package tech.biblio.BookListing.external.dto;

import java.util.List;

public record SearchResponseDTO(List<SearchResponseDataDTO> data, String error) {
}
