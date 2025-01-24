package tech.biblio.BookListing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.entities.Book;
import tech.biblio.BookListing.repositories.BookRepository;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book getBookByBookId(String bookId){
        return bookRepository.findByBookId(bookId);
    }

    public Book saveBook(Book book){
        Book dbBook = this.getBookByBookId(book.getBookId());
        if(dbBook!=null) {
           return dbBook;
        }
        return bookRepository.save(book);
    }
}
