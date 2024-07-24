package tech.biblio.BookListing.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.biblio.BookListing.contants.ApplicationConstants;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JWTValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
           String jwtToken = request.getHeader(ApplicationConstants.JWT_HEADER);
           if(jwtToken!=null) {
               Environment env = getEnvironment();
               String secret = env.getProperty(ApplicationConstants.JWT_SECRET,
                       ApplicationConstants.JWT_SECRET_DEFAULT);
               SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
               if(secretKey!=null){
                   Claims claims = Jwts.parser().verifyWith(secretKey)
                           .build().parseSignedClaims(jwtToken).getPayload();
                   String username = String.valueOf(claims.get("username"));
                   String authorities = String.valueOf(claims.get("authorities"));
                   log.info("Username : {}",username);
                   log.info("Authorities : {}", authorities);
                   UsernamePasswordAuthenticationToken authenticationToken = new
                           UsernamePasswordAuthenticationToken(username,null,
                           AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                   SecurityContextHolder.getContext().setAuthentication(authenticationToken);
               }
           }
        }catch (Exception e){
            throw new BadCredentialsException("Invalid Token Received!");
        }
        filterChain.doFilter(request,response);
    }

    /**
     * Used for Prevent the filter for certain conditions. The conditions where the method returns true,
     * the current filter is NOT RUN
     * @Returns true for /user URL, false otherwise
     * @Params HttpServletRequest request
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/user");
    }
}
