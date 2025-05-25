package tech.biblio.BookListing.utils;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@NoArgsConstructor
@Service
public class RedisUtil {
    @Value("${REDIS_PATH:localhost}")
    private String redisPath;

    @Value("${REDIS_PORT:6379}")
    private String redisPort;

    @Value("${REDIS_AUTH_USERNAME:username}")
    private String redisAuthUsername;

    @Value("${REDIS_AUTH_KEY:authkey}")
    private String redisAuthKey;

    private RedisClient redisClient;

    public RedisClient getClient() throws ResourceAccessException {

        if (redisAuthKey.isEmpty() || redisPath.isEmpty() || redisPort.isEmpty()) {
            throw new ResourceAccessException("Error accessing redis config data");
        }
        if (this.redisClient != null) {
            return this.redisClient;
        }
        RedisURI uri = RedisURI.Builder
                .redis(redisPath, Integer.parseInt(redisPort))
                .withAuthentication(redisAuthUsername, redisAuthKey)
                .build();
        this.redisClient = RedisClient.create(uri);
        return this.redisClient;
    }
}
