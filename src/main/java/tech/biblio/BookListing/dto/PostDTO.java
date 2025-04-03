package tech.biblio.BookListing.dto;

import tech.biblio.BookListing.entities.Book;

import java.time.LocalDateTime;

public record PostDTO(String id, String title, String content, int likes, String[] comments, Book book, String coverImage, String slug, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static PostDTOBuilder builder() {
        return new PostDTOBuilder();
    }
    public static class PostDTOBuilder {
        private String id;
        private String title;
        private String content;
        private int likes;
        private String[] comments;

        private Book book;

        private String coverImage;

        private String slug;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public PostDTOBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PostDTOBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PostDTOBuilder content(String content) {
            this.content = content;
            return this;
        }

        public PostDTOBuilder likes(int likes) {
            this.likes = likes;
            return this;
        }

        public PostDTOBuilder comments(String[] comments) {
            this.comments = comments;
            return this;
        }

        public PostDTOBuilder book(Book book){
            this.book = book;
            return this;
        }

        public PostDTOBuilder coverImage(String coverImage){
            this.coverImage = coverImage;
            return this;
        }

        public PostDTOBuilder slug(String slug){
            this.slug = slug;
            return this;
        }

        public PostDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PostDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public PostDTO build() {
            return new PostDTO(id, title, content, likes, comments, book, coverImage, slug, createdAt, updatedAt);
        }
    }
}
