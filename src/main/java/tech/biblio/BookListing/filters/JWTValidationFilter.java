package tech.biblio.BookListing.filters;

import io.jsonwebtoken.Claims;
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
import tech.biblio.BookListing.utils.JwtUtils;

import java.io.IOException;

@Slf4j
public class JWTValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
           String authHeader = request.getHeader(ApplicationConstants.JWT_HEADER);
            JwtUtils jwtUtils = new JwtUtils();

           if(authHeader!=null) {
               authHeader = authHeader.trim();

               String jwtToken = authHeader.substring(Math.min(authHeader.length()-1, 6));
               jwtToken = jwtToken.trim();
               Environment env = getEnvironment();
               System.out.println("Access Token : |"+jwtToken);
               boolean isValidAccessToken = jwtUtils.validateAccessToken(jwtToken, env); // throws Exception if Invalid, return false if Expired
               Claims claims = jwtUtils.getClaimsFromJwt(jwtToken,env);
               if(claims==null){
                   throw new BadCredentialsException("Invalid Token Received!");
               }
               String username = String.valueOf(claims.get("username"));
               String authorities = String.valueOf(claims.get("authorities"));
               log.info("Username : {}",username);
               log.info("Authorities : {}", authorities);
               UsernamePasswordAuthenticationToken authenticationToken = new
                       UsernamePasswordAuthenticationToken(username,null,
                       AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
               SecurityContextHolder.getContext().setAuthentication(authenticationToken);
           }
        }finally {

        }
        filterChain.doFilter(request,response);
    }
    private void handleAccessDenied(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set the status code
        response.setContentType("application/json");
        log.error(
                "{\"status\":\"UNAUTHORIZED\",\"message\":\"" + message + "\",\"error\":\"Access Denied\"}");
    }
    /**
     * Used for Prevent the filter for certain conditions. The conditions where the method returns true,
     * the current filter is NOT RUN
     * @Returns true for /user URL, false otherwise
     * @Params HttpServletRequest request
     */
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
////        return request.getServletPath().equals("/user");
//    }
}
