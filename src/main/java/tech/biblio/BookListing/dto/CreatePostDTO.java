package tech.biblio.BookListing.dto;

import tech.biblio.BookListing.entities.Book;

public record CreatePostDTO(String title, String content, Book taggedBook, String coverImage) {
}
