package tech.biblio.BookListing.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CsrfCookieFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            String csrfCookieValue = csrfToken.getToken();

            // Manually create the Set-Cookie header with SameSite=None
            String setCookieHeader = String.format(
                    "XSRF-TOKEN=%s; Path=/; HttpOnly; Secure; SameSite=None",
                    csrfCookieValue
            );
            response.setHeader("Set-Cookie", setCookieHeader);
        }

        filterChain.doFilter(request, response);
    }
}
