package tech.biblio.BookListing.entities;

public class Book {
    private Long _id;
    private String name;
    private String description;
    /*The ISBN is a ten-digit unique number.
    With the help of the ISBN, we can easily find any book.
     The ISBN is a legal number when
     1*Digit1 + 2*Digit2 + 3*Digit3 +
     4*Digit4 + 5*Digit5 + 6*Digit6 +
     7*Digit7 + 8*Digit8 + 9*Digit9 + 10*Digit10
     is divisible by 11.
     The digits are taken from right to left.
     So, if the ten-digit number is 7426985414,
     Digit1 and Digit10 will be 4 and 7, respectively.
     */
    private Long isbn;
    private String author;

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public Long getIsbn() {
        return isbn;
    }

    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
// optional : Add Tags - Science, Fiction, Autobiography, Academic

    @Override
    public String toString() {
        return "Book{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isbn=" + isbn +
                ", author='" + author + '\'' +
                '}';
    }
}
