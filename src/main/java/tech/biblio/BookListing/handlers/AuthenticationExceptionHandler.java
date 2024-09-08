package tech.biblio.BookListing.handlers;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.dto.ErrorResponse;
import tech.biblio.BookListing.exceptions.AccessTokenValidationException;
import tech.biblio.BookListing.exceptions.RefreshTokenValidationException;

@NoArgsConstructor
@Component
public class AuthenticationExceptionHandler {
    public ErrorResponse handler(Exception e){

        if(e instanceof AccessTokenValidationException){
            return ErrorResponse.builder()
                    .error("Invalid Access Token")
                    .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                    .errorDescription("Session expired, trying to Login again.")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }
        else if(e instanceof RefreshTokenValidationException){
            return ErrorResponse.builder()
                    .error("Invalid Refresh Token")
                    .status(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .errorDescription("Logged out, please login again to proceed")
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        else if(e instanceof AccessDeniedException){
            return ErrorResponse.builder()
                    .error("Access Denied")
                    .status(HttpStatus.FORBIDDEN.getReasonPhrase())
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .errorDescription("You do not have access, login or connect with admin.").build();
        }
        else return null;
    }
}
