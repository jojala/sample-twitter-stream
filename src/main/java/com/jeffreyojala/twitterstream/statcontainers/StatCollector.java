package com.jeffreyojala.twitterstream.statcontainers;

import com.jeffreyojala.twitterstream.models.CollectedStats;
import com.jeffreyojala.twitterstream.models.Domain;
import com.jeffreyojala.twitterstream.models.Emoji;
import com.jeffreyojala.twitterstream.models.HashTag;
import com.jeffreyojala.twitterstream.models.Rank;
import com.jeffreyojala.twitterstream.processor.EmojiStats;
import com.jeffreyojala.twitterstream.processor.HashTagStats;
import com.jeffreyojala.twitterstream.processor.ReceivedStats;
import com.jeffreyojala.twitterstream.processor.UrlStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class StatCollector {

    @Autowired
    private EmojiStats emojiStats;

    @Autowired
    private HashTagStats hashTagStats;

    @Autowired
    private ReceivedStats receivedStats;

    @Autowired
    private UrlStats urlStats;

    public CollectedStats collectStats() {

        CollectedStats collectedStats = new CollectedStats();

        //    Total number of tweets received
        collectedStats.totalNumberOfTweetsReceived = receivedStats.getTotalNumberOfTweets();

        //    Average tweets per hour/minute/second
        collectedStats.currentTweetsPerSecond = receivedStats.getCurrentTweetsPerSecond();
        collectedStats.currentTweetsPerMinute = receivedStats.getCurrentTweetsPerMinute();
        collectedStats.currentTweetsPerHour = receivedStats.getCurrentTweetsPerHour();

        //    Top emojis in tweets
        Map<Integer,Rank<Emoji>> topTenEmojis = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            Optional<Rank<Emoji>> emojiRank = emojiStats.getEmojiByRank(i);
            if (emojiRank.isPresent()) {
                topTenEmojis.put(i, emojiRank.get());
            }
        }
        collectedStats.topTenEmojis = topTenEmojis;

        //    Percent of tweets that contains emojis
        collectedStats.percentageOfTweetsThatContainEmojis = emojiStats.getPercentageOfTweetsThatContainEmojis();

        //    Top hashtags
        Map<Integer,Rank<HashTag>> topTenHashTags = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            Optional<Rank<HashTag>> hashTagRank = hashTagStats.getHashtagByRank(i);
            if (hashTagRank.isPresent()) {
                topTenHashTags.put(i, hashTagRank.get());
            }
        }
        collectedStats.topTenHashTags = topTenHashTags;

        //    Percent of tweets that contain a url
        collectedStats.percentageOfTweetsThatContainAUrl = urlStats.getPercentageOfTweetsThatContainUrls();

        //    Percent of tweets that contain a photo url (pic.twitter.com, pbs.twimg.com, or instagram)
        collectedStats.percentageOfTweetsThatContainAPhotoUrl = urlStats.getPercentageOfTweetsThatContainPhotoUrls();

        //    Top domains of urls in tweets
        Map<Integer,Rank<Domain>> topTenDomains = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            Optional<Rank<Domain>> domainRank = urlStats.getDomainByRank(i);
            if (domainRank.isPresent()) {
                topTenDomains.put(i, domainRank.get());
            }
        }
        collectedStats.topTenDomains = topTenDomains;

        return collectedStats;
    }
}
