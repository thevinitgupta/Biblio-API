package tech.biblio.BookListing.dto;

import org.springframework.web.multipart.MultipartFile;

public record ImageUpload(MultipartFile multipartFile) {
}
