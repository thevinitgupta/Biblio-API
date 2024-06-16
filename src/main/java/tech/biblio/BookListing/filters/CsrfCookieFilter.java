package tech.biblio.BookListing.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class CsrfCookieFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
//            System.out.println(request.getAttribute("XSRF-TOKEN"));
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName()+" -> "+cookie.getValue());
                if(cookie.getName().equals("XSRF-TOKEN")) {
                    response
                            .setHeader(csrfToken.getHeaderName(), cookie.getValue());
                }
            }
            System.out.println(CsrfToken.class.getName());
        }

//        if(null!=csrfToken){
//            System.out.println(csrfToken.getHeaderName());
//            response
//                    .setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
//        }
        filterChain
                .doFilter(request,response);
    }
}
