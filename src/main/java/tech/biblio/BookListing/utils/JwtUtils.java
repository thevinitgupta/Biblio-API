package tech.biblio.BookListing.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.constants.ApplicationConstants;
import tech.biblio.BookListing.exceptions.AccessTokenValidationException;
import tech.biblio.BookListing.exceptions.RefreshTokenValidationException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@Component
@Slf4j
public class JwtUtils {




    public String generateAccessToken(HashMap<String ,Object> claims, Environment environment){
        if(!claims.containsKey("username")) throw new IllegalArgumentException("Username Missing");
        String accessTokenExpiry = ApplicationConstants.ACCESS_TOKEN_EXPIRY;
        long accessTokenTime = Long.parseLong(Objects.requireNonNull(environment.getProperty(accessTokenExpiry)));
        return generateJwtTokenFromUsername("Access-Token", claims, accessTokenTime, environment);
    }

    public String generateRefreshToken(HashMap<String ,Object> claims, Environment environment){
        String refreshTokenExpiry = ApplicationConstants.REFRESH_TOKEN_EXPIRY;
        long refreshTokenTime = Long.parseLong(Objects.requireNonNull(environment.getProperty(refreshTokenExpiry)));
        return generateJwtTokenFromUsername("Refresh-Token", claims, refreshTokenTime, environment);
    }

    private String generateJwtTokenFromUsername( String subject, HashMap<String ,Object> claims, long tokenExpiry, Environment env){
        if(env!=null){
            String secret = env.getProperty(ApplicationConstants.JWT_SECRET,
                    ApplicationConstants.JWT_SECRET_DEFAULT);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            return Jwts.builder()
                    .issuer("Biblio")
                    .subject(subject)
                    .claims(claims)
                    // add username and authorities in the claims object being passed for Access Token, only keep username for Refresh Token
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

    public String getTokenIdFromJwt(String refreshToken, Environment environment){
        if(environment!=null) {
            String secret = environment.getProperty(ApplicationConstants.JWT_SECRET,
                    ApplicationConstants.JWT_SECRET_DEFAULT);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(refreshToken).getPayload();
            return (String) claims.get("token-id");
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
            validationMessage = "";
            log.error("Expired Access Token" +" : {}",e.getMessage());
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(),"Expired Access Token");
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

    public boolean validateRefreshToken(String jwtToken, Environment environment){
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
            validationMessage = "Expired Refresh Token";
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
