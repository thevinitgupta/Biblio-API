package tech.biblio.BookListing.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.biblio.BookListing.constants.ApplicationConstants;
import tech.biblio.BookListing.utils.JwtUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

public class JWTGenerationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authentication!=null && (authHeader==null || authHeader.substring(6).isEmpty())){
            Environment env = getEnvironment();
            if(env!=null){
                HashMap<String, Object> accessTokenClaims = new HashMap<>();
                accessTokenClaims.put("username", authentication.getName());
                accessTokenClaims.put("authorities", authentication.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")));
                JwtUtils jwtUtils = new JwtUtils();
                String jwtToken = jwtUtils.generateAccessToken(accessTokenClaims, env);
                System.out.println("New JWT Token: "+jwtToken);
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
