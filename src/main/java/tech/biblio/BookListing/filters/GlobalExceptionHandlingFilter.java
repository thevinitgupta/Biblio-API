package tech.biblio.BookListing.filters;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.biblio.BookListing.dto.ErrorResponse;
import tech.biblio.BookListing.utils.JsonConverter;

import java.io.IOException;

@Component // Set a high order value to make it the last filter
@NoArgsConstructor
public class GlobalExceptionHandlingFilter extends OncePerRequestFilter {



    private void handleException(HttpServletResponse response, Exception ex) throws IOException {
        JsonConverter jsonConverter = new JsonConverter();
        ErrorResponse errorResponse = new ErrorResponse();
        if(ex instanceof AccessDeniedException){
            System.out.println("Access Denied Exception in Filters");
        errorResponse.setError("Access Denied");
        errorResponse.setErrorDescription(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else if(ex instanceof JwtException){
            errorResponse.setError("Access Denied");
            errorResponse.setErrorDescription(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        else {
            errorResponse.setError(String.valueOf(UnknownError.class));
            errorResponse.setErrorDescription("An Unknown error occurred : "+ex.getLocalizedMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        response.setContentType("application/json");


        response.getWriter().write(jsonConverter.getJsonObject(errorResponse));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            System.out.println("Global Exception Filter : ");
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            handleException(response, ex);
        }
    }
}

