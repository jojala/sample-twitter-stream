package com.jeffreyojala.twitterstream.processor;

import com.jeffreyojala.twitterstream.models.HashTag;
import com.jeffreyojala.twitterstream.models.Rank;
import com.jeffreyojala.twitterstream.statcontainers.Ranking;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.Arrays;
import java.util.Optional;

/**
 * Top hashtags (count)
 */
@Component
public class HashTagStats implements StatStep {

    @Bean
    private Ranking<HashTag> ranking() {
        return ranking;
    }
    private Ranking<HashTag> ranking = new Ranking<>();

    public Optional<Rank<HashTag>> getHashtagByRank(int rank) {
       return ranking.getValueByRank(rank);
    }

    @Override
    public void process(Status status) {

        // count each of our hash tags
        Arrays.asList(status.getHashtagEntities()).forEach(hashtagEntity -> ranking.countValue(new HashTag(hashtagEntity.getText())));
    }

    @Override
    public void reset() {
        ranking.reset();
    }

    void forceStatCalculations() {
        ranking.rank();
    }
}
