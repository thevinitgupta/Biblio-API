package tech.biblio.BookListing.aspect;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.biblio.BookListing.config.RateLimiterConfig;
import tech.biblio.BookListing.constants.ApplicationConstants;
import tech.biblio.BookListing.exceptions.RateLimitExceededException;
import tech.biblio.BookListing.utils.JwtUtils;

@Aspect
@Component
@Order(1)
public class RateLimiterAspect {
    @Value("${JWT_SECRET}")
    String jwtSecretKey;


    private final RateLimiterConfig rateLimiterConfig;

    public RateLimiterAspect(@Lazy RateLimiterConfig rateLimiterConfig) {
        this.rateLimiterConfig = rateLimiterConfig;
    }

    @Around("@annotation(tech.biblio.BookListing.annotations.RateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();

        String rateLimitKey = getRateLimitKey(request);
        System.out.println("Rate Limit key : " + rateLimitKey);
        Bucket bucket = getOrCreateBucket(rateLimitKey);

        if (bucket.tryConsume(1L)) {
            return joinPoint.proceed();
        } else {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getResponse();
            if (response != null) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            }
            throw new RateLimitExceededException("Rate Limit Exceeded, please try again after some time");
        }
    }

    private String getRateLimitKey(HttpServletRequest request) {
        String authHeader = request.getHeader(ApplicationConstants.JWT_HEADER);
        JwtUtils jwtUtils = new JwtUtils();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7).trim();  // Remove "Bearer " prefix

            // 🔹 Extract username from JWT token
            String username = jwtUtils.getUsernameFromAccessToken(jwtToken, jwtSecretKey);
            if (username != null) {
                return "USER:" + username;  // User-based rate limiting
            } else {
                throw new BadCredentialsException("Invalid Token Received!");
            }
        }

        // 🔹 If no valid JWT, fallback to IP-based rate limiting
        String ipAddress = request.getRemoteAddr();
        return "IP:" + ipAddress;
    }

    private Bucket getOrCreateBucket(String clientKey) {
        return rateLimiterConfig.lettuceBasedProxyManager().builder().build(
                clientKey,
                () -> rateLimiterConfig.bucketConfigurationSupplier().get()
        );
    }

}
