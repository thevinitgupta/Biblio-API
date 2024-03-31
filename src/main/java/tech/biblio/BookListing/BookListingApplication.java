package tech.biblio.BookListing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@RequestMapping("api/v1")
public class BookListingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookListingApplication.class, args);
	}

}
