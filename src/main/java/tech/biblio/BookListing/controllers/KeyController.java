package tech.biblio.BookListing.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.biblio.BookListing.services.KeyService;

@RestController
@RequestMapping("key")
public class KeyController {

    private final KeyService keyService;

    public KeyController(KeyService keyService) {
        this.keyService = keyService;
    }

    @GetMapping("public")
    public ResponseEntity<String> getPublicKey() {
        return new ResponseEntity<>(keyService.getPublicKeyBase64(), HttpStatus.OK);
    }


}
