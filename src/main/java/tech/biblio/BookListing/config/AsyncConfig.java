package tech.biblio.BookListing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tech.biblio.BookListing.handlers.AsyncTaskHandler;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Bean(name = "postQueueExecutor")
    public Executor postQueueExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(7);
        executor.setQueueCapacity(20);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("AsyncPostExecutor-");
        executor.setRejectedExecutionHandler(AsyncTaskHandler::rejection);
        executor.initialize();
        return executor;
    }
}
