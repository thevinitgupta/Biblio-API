package tech.biblio.BookListing.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.contants.ApplicationConstants;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.core.env.Environment;

@Component
@Slf4j
public class JwtUtils {


    @Autowired
    private Environment env;

    public String generateJwtToken(Authentication userDetails){
        return generateJwtTokenFromUsername(userDetails);
    }

    public String generateJwtTokenFromUsername(Authentication userDetails){
        if(env!=null){
            String secret = env.getProperty(ApplicationConstants.JWT_SECRET,
                    ApplicationConstants.JWT_SECRET_DEFAULT);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            String accessTokenExpiry = ApplicationConstants.ACCESS_TOKEN_EXPIRY;
            Long tokenExpiry = Long.parseLong(env.getProperty(accessTokenExpiry));
            return Jwts.builder()
                    .issuer("Biblio")
                    .subject("Session Token")
                    .claim("username", userDetails.getName())
                    .claim("authorities", userDetails.getAuthorities()
                            .stream().map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(",")))
                    .issuedAt(new Date())
                    .expiration(new Date(new Date().getTime()+tokenExpiry))
                    .signWith(secretKey).compact();
        }
        return null;
    }

    public String getUsernameFromJwt(String jwtToken){
        String secret = env.getProperty(ApplicationConstants.JWT_SECRET,
                ApplicationConstants.JWT_SECRET_DEFAULT);
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwtToken).getPayload();
        return claims.getSubject();
    }


    public boolean validateJwtToken(String jwtToken){
        try {
            String secret = env.getProperty(ApplicationConstants.JWT_SECRET,
                    ApplicationConstants.JWT_SECRET_DEFAULT);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwtToken);
            return true;
        }catch (SignatureException e){
            log.error("Invalid JWT Signature : {}",e.getMessage());
        }catch (MalformedJwtException e){
            log.error("Invalid JWT Token : {}",e.getMessage());
        }catch (ExpiredJwtException e){
            log.error("Expired JWT Token : {}",e.getMessage());
        }catch (UnsupportedJwtException e){
            log.error("JWT Token is unsupported : {}",e.getMessage());
        }catch (IllegalArgumentException e){
            log.error("JWT claims string is empty : {}",e.getMessage());
        }
        return  false;
    }
}
