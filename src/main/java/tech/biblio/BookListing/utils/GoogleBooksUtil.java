package tech.biblio.BookListing.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.biblio.BookListing.exceptions.GoogleApiBooksException;

@Service
public class GoogleBooksUtil {

    private final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    public String getBooksData(String searchTerm, String apiKey) throws GoogleApiBooksException {
        RestTemplate restTemplate = new RestTemplate();

        // Construct the API URL with the provided search term
        String apiUrl = String.format(
                "%s?q=%s&fields=items(id,volumeInfo(title,subtitle,authors,publishedDate,imageLinks,industryIdentifiers))&key=%s",
                BASE_URL,
                searchTerm,
                apiKey
        );

        // Make the API call and return the response as a string

        return restTemplate.getForObject(apiUrl, String.class);
        /*
        return """
{
  "items": [
    {
      "id": "6cguEAAAQBAJ",
      "volumeInfo": {
        "title": "Har Dayal: The Great Revolutionary",
        "authors": [
          "E. Jaiwant Paul",
          "Shubh Paul"
        ],
        "publishedDate": "2003-01-01",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9788194566144"
          },
          {
            "type": "ISBN_10",
            "identifier": "8194566142"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=6cguEAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=6cguEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "1YVLAwAAQBAJ",
      "volumeInfo": {
        "title": "God, Heaven, and Har Magedon",
        "subtitle": "A Covenantal Tale of Cosmos and Telos",
        "authors": [
          "Meredith G. Kline"
        ],
        "publishedDate": "2006-03-15",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781597524780"
          },
          {
            "type": "ISBN_10",
            "identifier": "1597524786"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=1YVLAwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=1YVLAwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "Lk_HDwAAQBAJ",
      "volumeInfo": {
        "title": "Mental training kan avslaija styrkorna du har och omrden du kan forbottra",
        "authors": [
          "Stig-Arne Kristoffersen"
        ],
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781794808027"
          },
          {
            "type": "ISBN_10",
            "identifier": "1794808027"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=Lk_HDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=Lk_HDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "yV__AAAAQBAJ",
      "volumeInfo": {
        "title": "Har-Moni's Story",
        "authors": [
          "Joy Lee Larocque"
        ],
        "publishedDate": "1984-01-01",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781622337149"
          },
          {
            "type": "ISBN_10",
            "identifier": "162233714X"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=yV__AAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=yV__AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "_A8VEAAAQBAJ",
      "volumeInfo": {
        "title": "Biography of Har Gobind Khorana",
        "subtitle": "Biography of Har Gobind Khorana: A Nobel Laureate's Inspiring Story",
        "authors": [
          "Nandini"
        ],
        "publishedDate": "2021-02-01",
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=_A8VEAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=_A8VEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "6cguEAAAQBAJ",
      "volumeInfo": {
        "title": "Har Dayal: The Great Revolutionary",
        "authors": [
          "E. Jaiwant Paul",
          "Shubh Paul"
        ],
        "publishedDate": "2003-01-01",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9788194566144"
          },
          {
            "type": "ISBN_10",
            "identifier": "8194566142"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=6cguEAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=6cguEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "1YVLAwAAQBAJ",
      "volumeInfo": {
        "title": "God, Heaven, and Har Magedon",
        "subtitle": "A Covenantal Tale of Cosmos and Telos",
        "authors": [
          "Meredith G. Kline"
        ],
        "publishedDate": "2006-03-15",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781597524780"
          },
          {
            "type": "ISBN_10",
            "identifier": "1597524786"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=1YVLAwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=1YVLAwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "Lk_HDwAAQBAJ",
      "volumeInfo": {
        "title": "Mental training kan avslaija styrkorna du har och omrden du kan forbottra",
        "authors": [
          "Stig-Arne Kristoffersen"
        ],
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781794808027"
          },
          {
            "type": "ISBN_10",
            "identifier": "1794808027"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=Lk_HDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=Lk_HDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "yV__AAAAQBAJ",
      "volumeInfo": {
        "title": "Har-Moni's Story",
        "authors": [
          "Joy Lee Larocque"
        ],
        "publishedDate": "1984-01-01",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781622337149"
          },
          {
            "type": "ISBN_10",
            "identifier": "162233714X"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=yV__AAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=yV__AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "_A8VEAAAQBAJ",
      "volumeInfo": {
        "title": "Biography of Har Gobind Khorana",
        "subtitle": "Biography of Har Gobind Khorana: A Nobel Laureate's Inspiring Story",
        "authors": [
          "Nandini"
        ],
        "publishedDate": "2021-02-01",
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=_A8VEAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=_A8VEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "6cguEAAAQBAJ",
      "volumeInfo": {
        "title": "Har Dayal: The Great Revolutionary",
        "authors": [
          "E. Jaiwant Paul",
          "Shubh Paul"
        ],
        "publishedDate": "2003-01-01",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9788194566144"
          },
          {
            "type": "ISBN_10",
            "identifier": "8194566142"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=6cguEAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=6cguEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "1YVLAwAAQBAJ",
      "volumeInfo": {
        "title": "God, Heaven, and Har Magedon",
        "subtitle": "A Covenantal Tale of Cosmos and Telos",
        "authors": [
          "Meredith G. Kline"
        ],
        "publishedDate": "2006-03-15",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781597524780"
          },
          {
            "type": "ISBN_10",
            "identifier": "1597524786"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=1YVLAwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=1YVLAwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "Lk_HDwAAQBAJ",
      "volumeInfo": {
        "title": "Mental training kan avslaija styrkorna du har och omrden du kan forbottra",
        "authors": [
          "Stig-Arne Kristoffersen"
        ],
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781794808027"
          },
          {
            "type": "ISBN_10",
            "identifier": "1794808027"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=Lk_HDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=Lk_HDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "yV__AAAAQBAJ",
      "volumeInfo": {
        "title": "Har-Moni's Story 3",
        "authors": [
          "Joy Lee Larocque"
        ],
        "publishedDate": "1984-01-01",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781622337149"
          },
          {
            "type": "ISBN_10",
            "identifier": "162233714X"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=yV__AAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=yV__AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "_A8VEAAAQBAJ",
      "volumeInfo": {
        "title": "Biography of Har Gobind Khorana 3",
        "subtitle": "Biography of Har Gobind Khorana: A Nobel Laureate's Inspiring Story",
        "authors": [
          "Nandini"
        ],
        "publishedDate": "2021-02-01",
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=_A8VEAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=_A8VEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "6cguEAAAQBAJ",
      "volumeInfo": {
        "title": "Har Dayal: The Great Revolutionary",
        "authors": [
          "E. Jaiwant Paul",
          "Shubh Paul"
        ],
        "publishedDate": "2003-01-01",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9788194566144"
          },
          {
            "type": "ISBN_10",
            "identifier": "8194566142"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=6cguEAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=6cguEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "1YVLAwAAQBAJ",
      "volumeInfo": {
        "title": "God, Heaven, and Har Magedon",
        "subtitle": "A Covenantal Tale of Cosmos and Telos",
        "authors": [
          "Meredith G. Kline"
        ],
        "publishedDate": "2006-03-15",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781597524780"
          },
          {
            "type": "ISBN_10",
            "identifier": "1597524786"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=1YVLAwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=1YVLAwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "Lk_HDwAAQBAJ",
      "volumeInfo": {
        "title": "4 Mental training kan avslaija styrkorna du har och omrden du kan forbottra",
        "authors": [
          "Stig-Arne Kristoffersen"
        ],
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781794808027"
          },
          {
            "type": "ISBN_10",
            "identifier": "1794808027"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=Lk_HDwAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=Lk_HDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "yV__AAAAQBAJ",
      "volumeInfo": {
        "title": "Har-Moni's Story 4",
        "authors": [
          "Joy Lee Larocque"
        ],
        "publishedDate": "1984-01-01",
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781622337149"
          },
          {
            "type": "ISBN_10",
            "identifier": "162233714X"
          }
        ],
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=yV__AAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=yV__AAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    },
    {
      "id": "_A8VEAAAQBAJ",
      "volumeInfo": {
        "title": "Biography of Har Gobind Khorana 4",
        "subtitle": "Biography of Har Gobind Khorana: A Nobel Laureate's Inspiring Story",
        "authors": [
          "Nandini"
        ],
        "publishedDate": "2021-02-01",
        "imageLinks": {
          "smallThumbnail": "http://books.google.com/books/content?id=_A8VEAAAQBAJ&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
          "thumbnail": "http://books.google.com/books/content?id=_A8VEAAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
        }
      }
    }
  ]
}
                
                """;
*/
    }

}
