package tech.biblio.BookListing.controllers;

import org.springframework.web.bind.annotation.*;
import tech.biblio.BookListing.entities.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {
    private Map<Long, Book> books = new HashMap<>();
    @GetMapping
    public List<Book> getAll(){
        return new ArrayList<>(books.values());
    }

    @PostMapping
    public boolean addBook(@RequestBody Book book){
        books.put(book.get_id(), book);
        return true;
    }

    @GetMapping(name = "bookbyid", value = "/id/{bookId}")
    public Book getBook(@PathVariable Long bookId){
        if(books.containsKey(bookId)) {
            return books.get(bookId);
        }
        return new Book();
    }

    @PutMapping(name = "updatebook", value = "/")
    public Book updateBook(@RequestBody Book book){
        Long bookId = book.get_id();
        if(books.containsKey(bookId)) {
            books.put(bookId, book);
            return book;
        }
        return new Book();
    }

    @DeleteMapping(name = "deleteBook", value = "/id/{bookId}")
    public boolean deleteBook(@PathVariable Long bookId){
        if(books.containsKey(bookId)) {
             books.remove(bookId);
             return true;
        }
        return false;
    }
}
