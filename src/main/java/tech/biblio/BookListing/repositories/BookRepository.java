package tech.biblio.BookListing.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import tech.biblio.BookListing.entities.Book;

public interface BookRepository extends MongoRepository<Book, ObjectId> {
    Book findByBookId(String bookId);
}
