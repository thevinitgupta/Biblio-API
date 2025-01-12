package tech.biblio.BookListing.dto;

public record PostDTO(String id, String title, String content, int likes, String[] comments) {
    public static PostDTOBuilder builder() {
        return new PostDTOBuilder();
    }
    public static class PostDTOBuilder {
        private String id;
        private String title;
        private String content;
        private int likes;
        private String[] comments;

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

        public PostDTO build() {
            return new PostDTO(id, title, content, likes, comments);
        }
    }
}
