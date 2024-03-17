package tech.biblio.BookListing.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Arrays;

@Document("post")
public class Post {
    @Id
    private String id;

    @Override
    public String toString() {
        return "Post{" +
                "bookId='" + bookId + '\'' +
                ", content='" + content + '\'' +
                ", image=" + image +
                ", likes=" + likes.length +
                ", comments=" + comments.length +
                '}';
    }

    @DocumentReference
    private String bookId;
    private String content;
    private Image image;
    private Like[] likes;
    private Comment[] comments;

    public Post(String id, String bookId, String content, Like[] likes, Comment[] comments) {
        this.id = id;
        this.bookId = bookId;
        this.content = content;
        this.likes = likes;
        this.comments = comments;
    }

    public Post(String id, String bookId, String content, Image image, Like[] likes, Comment[] comments) {
        this.id = id;
        this.bookId = bookId;
        this.content = content;
        this.image = image;
        this.likes = likes;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Like[] getLikes() {
        return likes;
    }

    public void setLikes(Like[] likes) {
        this.likes = likes;
    }

    public Comment[] getComments() {
        return comments;
    }

    public void setComments(Comment[] comments) {
        this.comments = comments;
    }
}
