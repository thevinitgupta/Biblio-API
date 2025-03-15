package tech.biblio.BookListing.dto;

public record PaginationDTO(int currentPage,
                            long totalItems,
                            int totalPages,
                            boolean hasMore) {
}
