package tech.biblio.BookListing.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.biblio.BookListing.dto.ErrorResponse;
import tech.biblio.BookListing.handlers.AuthenticationExceptionHandler;
import tech.biblio.BookListing.handlers.InvalidValueExceptionHandler;
import tech.biblio.BookListing.handlers.ResourceExceptionHandler;
import tech.biblio.BookListing.utils.JsonConverter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component // Set a high order value to make it the last filter
@NoArgsConstructor
@Slf4j
public class GlobalExceptionHandlingFilter extends OncePerRequestFilter {

    @Autowired
    public AuthenticationExceptionHandler authenticationExceptionHandler;

    @Autowired
    public ResourceExceptionHandler resourceExceptionHandler;

    @Autowired
    public InvalidValueExceptionHandler invalidValueExceptionHandler;


    private void handleException(HttpServletResponse response, Exception ex) throws IOException {
        JsonConverter jsonConverter = new JsonConverter();
        ErrorResponse errorResponse = null;

        errorResponse = authenticationExceptionHandler.handler(ex);
        if(errorResponse==null) {
            errorResponse = resourceExceptionHandler.handler(ex);
        }
        if(errorResponse==null){
            errorResponse = invalidValueExceptionHandler.handler(ex);
        }
        if(errorResponse==null){
            log.error("Unknown Exception in filter : {}, {}", ex.getMessage(), LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            errorResponse = ErrorResponse.builder()
                    .error("Unknown Error")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                    .errorDescription(ex.getLocalizedMessage())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
        response.setContentType("application/json");
        response.setStatus(errorResponse.getHttpStatus().value());

        response.getWriter().write(jsonConverter.getJsonObject(errorResponse));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            handleException(response, ex);
        }
    }
}

