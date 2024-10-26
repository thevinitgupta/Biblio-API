package tech.biblio.BookListing.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.biblio.BookListing.utils.JwtUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class RequestValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        JwtUtils jwtUtils= new JwtUtils();
        try{
            String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if(authHeader!=null && StringUtils.startsWith(authHeader, "Basic ")){
                authHeader = authHeader.trim();
                String token = authHeader.substring(6);
                Environment env = getEnvironment();
                String email = jwtUtils.getUsernameFromJwt(token, env);
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
        finally {
            filterChain.doFilter(request,response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.contains("/auth");
    }
}
