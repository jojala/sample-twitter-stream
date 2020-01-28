package com.jeffreyojala.twitterstream.processor;

import com.jeffreyojala.twitterstream.aspects.LogExecutionTime;
import com.jeffreyojala.twitterstream.models.Emoji;
import com.jeffreyojala.twitterstream.models.Rank;
import com.jeffreyojala.twitterstream.services.EmojiMaster;
import com.jeffreyojala.twitterstream.statcontainers.Percent;
import com.jeffreyojala.twitterstream.statcontainers.Ranking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Top emojis in tweets
 * Percent of tweets that contains emojis (count)
 */
@Component
public class EmojiStats implements StatStep {

    @Autowired
    private EmojiMaster emojiMaster;

    @Bean
    private Ranking<Emoji> emojiRanking() {
        return emojiRanking;
    }
    private Ranking<Emoji> emojiRanking = new Ranking<>();
    private Percent containEmojis = new Percent();

    public Optional<Rank<Emoji>> getEmojiByRank(int rank) {
        return emojiRanking.getValueByRank(rank);
    }

    public double getPercentageOfTweetsThatContainEmojis() {
        return containEmojis.getPercentage();
    }

    @Override
    public void process(Status status) {

        List<Emoji> emojis = extractEmojiStats(status.getText());

        // rank our emojis
        emojis.forEach(emoji -> {
            emojiRanking.countValue(emoji);
        });

        // keep track of our percentage of tweets that contain emojis
        containEmojis.increment(emojis != null && emojis.size() > 0);
    }

    private List<Emoji> extractEmojiStats(String tweetText) {

        List<Emoji> emojis = new ArrayList<>();
        tweetText.codePoints().forEach(codePoint -> {
            Optional<Emoji> emoji = emojiMaster.getEmojiForCodePoint(codePoint);
            if (emoji.isPresent()) {
                emojis.add(emoji.get());
            }
        });

        return emojis;
    }

    @Override
    public void reset() {
        emojiRanking.reset();
        containEmojis.reset();
    }

    void forceStatCalculations() {
        emojiRanking.rank();
    }
}
