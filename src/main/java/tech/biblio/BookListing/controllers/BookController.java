package tech.biblio.BookListing.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.biblio.BookListing.dto.ResponseDTO;
import tech.biblio.BookListing.exceptions.GoogleApiBooksException;
import tech.biblio.BookListing.utils.GoogleBooksUtil;

@RestController
@RequestMapping("book")
@Slf4j
public class BookController {

    @Autowired
    private GoogleBooksUtil googleBooksUtil;

    @Value("${GOOGLE_BOOKS_API_KEY}")
    private String googleBooksApiKey;

    @GetMapping("search")
    public ResponseEntity<?> searchBooks(@RequestParam String query) throws GoogleApiBooksException {
        System.out.println("Google Books API Key : "+googleBooksApiKey);
        if(query==null || query.isEmpty()){
            return new ResponseEntity<>(
                    new ResponseDTO(HttpStatus.BAD_REQUEST.getReasonPhrase(),"Empty Search String"),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(googleBooksUtil.getBooksData(query+"&maxResults=5", googleBooksApiKey), HttpStatus.OK );
    }
}
