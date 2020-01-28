package com.jeffreyojala.twitterstream.statcontainers;

import java.util.concurrent.atomic.AtomicInteger;

public class Percent implements StatContainer {

    // the total number of events
    private AtomicInteger total = new AtomicInteger(0);

    // the count of events where the predicate is true
    private AtomicInteger count = new AtomicInteger(0);

    /**
     * Increment our total and if the predicate for which we are track is true also increment our count
     * @param predicate
     */
    public void increment(Boolean predicate) {

        total.incrementAndGet();

        if (predicate) {
            count.incrementAndGet();
        }
    }

    /**
     * Get percentage of predicated events
     * @return
     */
    public double getPercentage() {

        if (total.get() == 0) {
            return 0;
        }

        return (double)count.get() / (double)total.get();
    }

    public int getTotal() {
        return total.get();
    }

    public int getCount() {
        return count.get();
    }

    public void reset() {
        total = new AtomicInteger(0);
        count = new AtomicInteger(0);
    }
}
