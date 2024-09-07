package tech.biblio.BookListing.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.biblio.BookListing.contants.ApplicationConstants;
import tech.biblio.BookListing.utils.JwtUtils;

import java.io.IOException;
import java.util.HashMap;

public class JWTGenerationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authentication!=null && (authHeader==null || authHeader.substring(6).isEmpty())){
            Environment env = getEnvironment();
            if(env!=null){
                JwtUtils jwtUtils = new JwtUtils();
                String jwtToken = jwtUtils.generateJwtToken(authentication,new HashMap<>());
                System.out.println("New JET Token: "+jwtToken);
                response.setHeader(ApplicationConstants.JWT_HEADER,jwtToken);
            }
        }
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/user");
    }
}
