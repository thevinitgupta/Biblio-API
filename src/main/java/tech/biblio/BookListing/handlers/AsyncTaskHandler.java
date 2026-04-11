package tech.biblio.BookListing.handlers;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class AsyncTaskHandler {

    public static void rejection(Runnable r, ThreadPoolExecutor executor1) {
        log.warn("-------------Post Task Rejection START --------------\n" +
                "New task addition failed {}" +
                "-------------Post Task Rejection END --------------", executor1.getTaskCount());
    }
}
