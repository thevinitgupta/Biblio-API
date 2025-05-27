package tech.biblio.BookListing.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RefreshTokenValidationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    System.out.println("Cookie Received : " + cookie.getValue());
                    if ("refresh-token".equals(cookie.getName())) {
                        // Found the refresh-token, handle it as needed
                        String refreshToken = cookie.getValue();
                        System.out.println("Refresh Token Received : " + refreshToken);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception while reading cookies");
        }
        filterChain.doFilter(request, response);
    }
}
