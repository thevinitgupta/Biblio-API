package tech.biblio.BookListing.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RequestValidationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try{
            String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
            authHeader = authHeader.trim();
            if(authHeader!=null && StringUtils.startsWith(authHeader, "Basic ")){
                byte [] base64 = authHeader.substring(6).getBytes(StandardCharsets.UTF_8);
                byte [] decoded = Base64.getDecoder().decode(base64);
                String token = new String(decoded, StandardCharsets.UTF_8); // username:password
                int delim = token.indexOf(":");
                if(delim==-1) {
                    throw new BadCredentialsException("Invalid Basic authentication Token");
                }
                String email = token.substring(0,delim);
                if(email.toLowerCase().contains("test")){
                    httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    httpResponse.setContentType("text/plain");

                    // Write the response content
                    try (PrintWriter out = httpResponse.getWriter()) {
                        out.write("Invalid Email Format - Cannot Contain Test");
                    }
                    return;
                }
            }
        }catch (IllegalArgumentException e){
            throw new BadCredentialsException("Failed to decode basic Authentication Token");
        }
        chain.doFilter(request,response);
    }
}
