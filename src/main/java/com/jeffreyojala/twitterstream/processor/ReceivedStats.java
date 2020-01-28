package com.jeffreyojala.twitterstream.processor;

import com.jeffreyojala.twitterstream.statcontainers.MovingAverage;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Total number of tweets received
 * Average tweets per hour/minute/second
 */
@Component
public class ReceivedStats implements StatStep {

    // Thread safe tweet count
    private AtomicInteger totalTweetCount = new AtomicInteger(0);

    // Moving average stats
    @Bean
    private MovingAverage movingAverage() {
        return movingAverage;
    }
    private MovingAverage movingAverage = new MovingAverage();

    // Total number of tweets received
    public int getTotalNumberOfTweets() {
        return totalTweetCount.get();
    }

    public double getCurrentTweetsPerSecond() {
        return movingAverage.getAvgPerSecond();
    }

    public double getCurrentTweetsPerMinute() {
        return movingAverage.getAvgPerMinute();
    }

    public double getCurrentTweetsPerHour() {
        return movingAverage.getAvgPerHour();
    }

    public void process(Status status) {

        // increment our total tweet counter
        totalTweetCount.incrementAndGet();

        // increment count registers
        movingAverage.increment();
    }

    @Override
    public void reset() {

        totalTweetCount = new AtomicInteger(0);
        movingAverage.reset();
    }

    void forceStatCalculations() {
        // ideally we'd force movingAverage to calculate stats, but this is very time dependentant... need to think on this one
    }
}
