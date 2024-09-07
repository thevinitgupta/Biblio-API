package tech.biblio.BookListing.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import tech.biblio.BookListing.dto.ErrorResponse;

public class JsonConverter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getJsonObject(ErrorResponse errorResponse) {
        try {
            // Convert ErrorResponse object to JSON string
            return objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            // Handle exception if serialization fails
            e.printStackTrace();
            return "{}"; // Return empty JSON on error
        }
    }
}

