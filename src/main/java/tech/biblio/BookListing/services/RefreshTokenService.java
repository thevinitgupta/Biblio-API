package tech.biblio.BookListing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.entities.RefreshTokenStore;
import tech.biblio.BookListing.exceptions.RefreshTokenValidationException;
import tech.biblio.BookListing.repositories.RefreshTokenRepository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    // TODO : handle DuplicateKeyException/DataIntegrityViolationException and MongoWriteException from where this is called
    public RefreshTokenStore saveToken(RefreshTokenStore refreshTokenStore) {
        return refreshTokenRepository.save(refreshTokenStore);
    }

    public RefreshTokenStore getTokenByTokenId(String tokenId) {
        return refreshTokenRepository.findFirstByTokenId(tokenId);
    }

    public boolean checkValidity(String tokenId) throws IllegalArgumentException {
        RefreshTokenStore dbToken = refreshTokenRepository.findFirstByTokenId(tokenId);
        if (dbToken == null) {
            throw new RefreshTokenValidationException("Token with ID not found");
        }
        Date now = new Date();
        long diffInMillis = Math.abs(now.getTime() - dbToken.getIssuedAt().getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        if (diff > 15) {
            this.invalidateToken(tokenId);
            dbToken.setIsValid(false);
        }
        // check token is valid or days difference <=15 days
        return dbToken.getIsValid() && diff <= 15;
    }

    public boolean invalidateToken(String tokenId) throws IllegalArgumentException {
        RefreshTokenStore dbToken = refreshTokenRepository.findFirstByTokenId(tokenId);
        if (dbToken == null) {
            throw new RefreshTokenValidationException("Token with ID not found");
        }
        dbToken.setIsValid(false);
        refreshTokenRepository.save(dbToken);
        return true;
    }
}
