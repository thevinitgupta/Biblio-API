package tech.biblio.BookListing.handlers;

import io.appwrite.exceptions.AppwriteException;
import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.biblio.BookListing.dto.ErrorResponse;
import tech.biblio.BookListing.exceptions.*;
import tech.biblio.BookListing.utils.JsonConverter;

import java.io.FileNotFoundException;
import java.util.MissingResourceException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    public AuthenticationExceptionHandler authenticationExceptionHandler;

    @Autowired
    public ResourceExceptionHandler resourceExceptionHandler;

    @Autowired
    public InvalidValueExceptionHandler invalidValueExceptionHandler;

    @Autowired
    public ServicesExceptionalHandler servicesExceptionalHandler;

    @Autowired
    public JsonConverter jsonConverter;

    @ExceptionHandler({
            UserNotFoundException.class,
            PostNotFoundException.class,
            MissingResourceException.class,
            FileNotFoundException.class,
            FileUploadException.class,
            BookUploadException.class,
            CommentNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception e) {
        ErrorResponse errorResponse = resourceExceptionHandler.handler(e);
        log.error(jsonConverter.getJsonObject(errorResponse));
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }


    @ExceptionHandler({
            AccessDeniedException.class,
            AccessTokenValidationException.class,
            RefreshTokenValidationException.class
    })
    public ResponseEntity<ErrorResponse> handleResourceException(Exception e) {
        ErrorResponse errorResponse = authenticationExceptionHandler.handler(e);
        log.error(jsonConverter.getJsonObject(errorResponse));
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler({
            AppwriteException.class,
            GoogleApiBooksException.class,
            RateLimitExceededException.class
    })
    public ResponseEntity<ErrorResponse> handleServicesException(Exception e) {
        ErrorResponse errorResponse = servicesExceptionalHandler.handler(e);
        log.error(jsonConverter.getJsonObject(errorResponse));
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler({
            StringIndexOutOfBoundsException.class,
            ArrayIndexOutOfBoundsException.class,
            NumberFormatException.class,
            NullPointerException.class,
            InvalidUserDetailsException.class,
            FileTypeNotAllowedException.class,
            IOException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidValueException(Exception e) {
        ErrorResponse errorResponse = invalidValueExceptionHandler.handler(e);
        log.error(jsonConverter.getJsonObject(errorResponse));
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e.getLocalizedMessage());
        return new ResponseEntity<>(
                "An unexpected error occurred: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
