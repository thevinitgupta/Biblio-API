package tech.biblio.BookListing.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.contants.ApplicationConstants;
import tech.biblio.BookListing.exceptions.AccessTokenValidationException;
import tech.biblio.BookListing.exceptions.RefreshTokenValidationException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtils {


    @Autowired
    private Environment env;

    public String generateJwtToken(Authentication userDetails, HashMap<String ,Object> claims){
        String accessTokenExpiry = ApplicationConstants.ACCESS_TOKEN_EXPIRY;
        long accessTokenTime = Long.parseLong(Objects.requireNonNull(env.getProperty(accessTokenExpiry)));
        return generateJwtTokenFromUsername(userDetails, "Access-Token", claims, accessTokenTime);
    }

    public String generateRefreshToken(Authentication userDetails, HashMap<String ,Object> claims){
        String refreshTokenExpiry = ApplicationConstants.REFRESH_TOKEN_EXPIRY;
        long refreshTokenTime = Long.parseLong(Objects.requireNonNull(env.getProperty(refreshTokenExpiry)));
        return generateJwtTokenFromUsername(userDetails, "Refresh-Token", claims, refreshTokenTime);
    }

    public String generateJwtTokenFromUsername(Authentication userDetails, String subject, HashMap<String ,Object> claims, long tokenExpiry){
        if(env!=null){
            String secret = env.getProperty(ApplicationConstants.JWT_SECRET,
                    ApplicationConstants.JWT_SECRET_DEFAULT);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            return Jwts.builder()
                    .issuer("Biblio")
                    .subject(subject)
                    .claims(claims)
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

    public String getUsernameFromJwt(String jwtToken, Environment environment){
        if(environment!=null) {
            String secret = environment.getProperty(ApplicationConstants.JWT_SECRET,
                    ApplicationConstants.JWT_SECRET_DEFAULT);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwtToken).getPayload();
            return (String) claims.get("username");
        }
        return null;
    }

    public Claims getClaimsFromJwt(String jwtToken, Environment environment){
        if(environment!=null) {
            System.out.println("Get Claims : "+jwtToken);
            String secret = environment.getProperty(ApplicationConstants.JWT_SECRET,
                    ApplicationConstants.JWT_SECRET_DEFAULT);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwtToken).getPayload();
        }
        return null;
    }


    public boolean validateAccessToken(String jwtToken, Environment environment){
        String validationMessage = "";
        try {
            String secret = environment.getProperty(ApplicationConstants.JWT_SECRET,
                    ApplicationConstants.JWT_SECRET_DEFAULT);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwtToken);
            return true;
        }catch (SignatureException e){
            validationMessage = "Invalid JWT Signature";
            log.error(validationMessage+" : {}", e.getMessage());
        }catch (MalformedJwtException e){
            validationMessage = "Invalid JWT Token";
            log.error(validationMessage+" : {}", e.getMessage());
        }catch (ExpiredJwtException e){
            validationMessage = "Expired JWT Token";
            log.error(validationMessage+" : {}",e.getMessage());
        }catch (UnsupportedJwtException e){
            validationMessage = "JWT Token is unsupported";
            log.error(validationMessage+" : {}",e.getMessage());
        }catch (IllegalArgumentException e){
            validationMessage = "JWT claims string is empty";
            log.error(validationMessage+" : {}",e.getMessage());
        }finally {
            if(!validationMessage.isEmpty()){
                throw new AccessTokenValidationException(validationMessage);
            }
        }
        return false;
    }

    public boolean validateRefreshToken(String jwtToken){
        String validationMessage = "";
        try {
            // TODO : Add Token Validation from Repository Logic
            String secret = env.getProperty(ApplicationConstants.JWT_SECRET,
                    ApplicationConstants.JWT_SECRET_DEFAULT);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwtToken);
            return true;
        }catch (SignatureException e){
            validationMessage = "Invalid JWT Signature";
            log.error(validationMessage+" : {}", e.getMessage());
        }catch (MalformedJwtException e){
            validationMessage = "Invalid JWT Token";
            log.error(validationMessage+" : {}", e.getMessage());
        }catch (ExpiredJwtException e){
            validationMessage = "Expired JWT Token";
            log.error(validationMessage+" : {}",e.getMessage());
        }catch (UnsupportedJwtException e){
            validationMessage = "JWT Token is unsupported";
            log.error(validationMessage+" : {}",e.getMessage());
        }catch (IllegalArgumentException e){
            validationMessage = "JWT claims string is empty";
            log.error(validationMessage+" : {}",e.getMessage());
        }finally {
            if(!validationMessage.isEmpty()){
                throw new RefreshTokenValidationException(validationMessage);
            }
        }
        return false;
    }
}
