package tech.biblio.BookListing.controllers;

import org.bson.json.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthCheck {
    @GetMapping("/health")
    public ResponseEntity<String> performCheck(){
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
