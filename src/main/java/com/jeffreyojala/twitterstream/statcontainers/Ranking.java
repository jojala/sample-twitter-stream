package com.jeffreyojala.twitterstream.statcontainers;

import com.jeffreyojala.twitterstream.models.Rank;
import com.jeffreyojala.twitterstream.models.Rankable;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Ranking<T extends Rankable> implements StatContainer {

    // Using "classic synchronization" instead of ConcurrentHashMap because I have to read and write
    // in a single step and don't want another thread to increment while I'm reading
    private Object mapLock = new Object();
    private Map<String,Rank<T>> counts = new HashMap<>();

    // rankings
    private Object rankingLock = new Object();
    private List<Rank<T>> rankings = new ArrayList<>();

    public void countValue(T value) {

        // read and increment
        synchronized (mapLock) {
            Rank<T> rank;
            if (counts.containsKey(value.getRankKey())) {
                rank = counts.get(value.getRankKey());
            }
            else {
                rank = new Rank<>(value, 0, 0);
                counts.put(value.getRankKey(), rank);
            }
            rank.setCount(rank.getCount() + 1);
        }
    }

    /**
     * Get value by rank where "rank" is 1 based
     * @param rank
     * @return
     */
    public Optional<Rank<T>> getValueByRank(int rank) {

        synchronized (rankingLock) {
            if (rank <= 0 || rank > rankings.size()) {
                return Optional.empty();
            }

            return Optional.of(rankings.get(rank - 1));
        }
    }

    public void reset() {

        synchronized (mapLock) {
            counts = new HashMap<>();
        }

        synchronized (rankingLock) {
            rankings = new ArrayList<>();
        }
    }

    /**
     * Calculate our rankings once a second.
     * NOTE: Depending on time-accurate we need the rankings
     * we could play games with increasing or decreasing the rate at which we update our rankings
     * for a balance of CPU load vs time-accuracy of data.
     */
    @Scheduled(fixedRate = 1000)
    public void rank() {

        // get sorted list
        synchronized (rankingLock) {
            rankings = counts.values().stream()
                    .sorted((r1, r2) -> Integer.compare(r2.getCount(), r1.getCount()))
                    .collect(Collectors.toList());

            // set rank
            AtomicInteger pos = new AtomicInteger(1);
            rankings.forEach(rank -> rank.setRank(pos.getAndIncrement()));
        }
    }
}
