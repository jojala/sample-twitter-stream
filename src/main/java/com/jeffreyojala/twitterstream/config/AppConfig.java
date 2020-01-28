package com.jeffreyojala.twitterstream.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public int tweetQueueDepth() {
        return 100000;
    }

    public int ingestExecutors() {
        return 4;
    }

    public int statProcessorExecutors() {
        return 4;
    }

    public static int statRefreshRate_millis() {
        return 1000;
    }
}
