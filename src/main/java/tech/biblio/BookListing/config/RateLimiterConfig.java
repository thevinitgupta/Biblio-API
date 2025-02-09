package tech.biblio.BookListing.config;

import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import tech.biblio.BookListing.utils.RedisUtil;

import java.time.Duration;
import java.util.function.Supplier;

@Configuration
public class RateLimiterConfig {

    private final RedisUtil redisUtil;

    public RateLimiterConfig(@Lazy RedisUtil redisUtil){
        this.redisUtil = redisUtil;
    }

    @Bean
    public ProxyManager<String> lettuceBasedProxyManager(){
        RedisClient redisClient = this.redisUtil.getClient();

        StatefulRedisConnection<String, byte[]> redisConnection =
                redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));

        return LettuceBasedProxyManager.builderFor(redisConnection).build();
    }

    @Bean
    public Supplier<BucketConfiguration> bucketConfigurationSupplier(){
        return () -> BucketConfiguration
                .builder()
                .addLimit(
                        BandwidthBuilder.builder()
                                .capacity(30L)  // Max 30 requests per day per user
                                .refillGreedy(1L, Duration.ofMinutes(2L))  // Refill 1 request every 2 min
                                .build()
                )
                // 🔹 Per IP: Max 50 searches/day
                .addLimit(
                        BandwidthBuilder.builder()
                                .capacity(50L)  // Max 50 requests per day per IP
                                .refillGreedy(1L, Duration.ofMinutes(2L))  // Refill 1 request every 2 min
                                .build()
                )
                // 🔹 Global App Limit: Max 900 requests per day
                .addLimit(
                        BandwidthBuilder.builder()
                                .capacity(900L)  // Max 900 requests per day for the whole app
                                .refillGreedy(1L, Duration.ofMinutes(2L))  // Refill 1 request every 2 min
                                .build()
                ).build();
    }

}
