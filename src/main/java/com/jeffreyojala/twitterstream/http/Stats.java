package com.jeffreyojala.twitterstream.http;


import com.jeffreyojala.twitterstream.models.CollectedStats;
import com.jeffreyojala.twitterstream.statcontainers.StatCollector;
import com.jeffreyojala.twitterstream.statcontainers.StatContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Stats {

    @Autowired
    private StatCollector statCollector;

    @GetMapping("/stats")
    public CollectedStats getStats() {
        return statCollector.collectStats();
    }
}
