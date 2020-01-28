package com.jeffreyojala.twitterstream.processor;

import com.jeffreyojala.twitterstream.models.Emoji;
import com.jeffreyojala.twitterstream.services.EmojiMaster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

import java.util.Date;
import java.util.Optional;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class EmojiTests {

    @Autowired
    private EmojiMaster emojiMaster;

    @Autowired
    private EmojiStats emojiStats;

    private final String THUMBS_UP = "\uD83D\uDC4D";
    private final String THUMBS_DOWN = "\uD83D\uDC4E";
    private final String BEAR_FACE = "\ud83d\udc3b";

    @BeforeEach()
    void clearStats() {
        emojiStats.reset();
    }

    @Test
    void emoijiFileLoads() {
        // NOTE: emoji file loads on bean post construct, so all we have to do is have a blank test
        //       here and wire in the emoji master bean and we'll test that case
    }

    @Test
    void identifyBearEmoji() throws Exception {
        int bearCodepoint = BEAR_FACE.codePointAt(0);
        Optional<Emoji> emoji = emojiMaster.getEmojiForCodePoint(bearCodepoint);

        // make sure we found our bear emoji
        if (!emoji.isPresent() && emoji.get().getName().compareTo("BEAR FACE") == 0) {
            throw new Exception("Could not find bear emoji");
        }
    }

    @Test
    void testEmojiRanking() throws Exception {

        String tweetText = "I tweet about " + THUMBS_UP + " emojis. " + THUMBS_UP + " But my number of followers is still " + THUMBS_DOWN;

        Status tweet = mockStatus(tweetText);
        emojiStats.process(tweet);

        // force our stat calculations
        emojiStats.forceStatCalculations();

        // make sure we found two emojis, and that the thumbs up emoji is ranked first and the second emoji
        // is ranked second
        if (emojiStats.getEmojiByRank(1).get().getValue().getName().compareTo("THUMBS UP SIGN") != 0 ||
                emojiStats.getEmojiByRank(2).get().getValue().getName().compareTo("THUMBS DOWN SIGN") != 0) {
            throw new Exception("Emoji ranks are not correct");
        }
    }

    @Test
    void testPercentageOfTweetsWithEmojis() throws Exception {


        String tweetText1 = "I tweet with " + THUMBS_UP + " emojis.";
        String tweetText2 = "I tweet with " + THUMBS_DOWN + " emojis.";
        String tweetText3 = "I don't tweet with emojis.";
        String tweetText4 = "I don't tweet about with emojis either.";

        Status tweet1 = mockStatus(tweetText1);
        Status tweet2 = mockStatus(tweetText2);
        Status tweet3 = mockStatus(tweetText3);
        Status tweet4 = mockStatus(tweetText4);

        emojiStats.process(tweet1);
        emojiStats.process(tweet2);
        emojiStats.process(tweet3);
        emojiStats.process(tweet4);

        // force our stat calculations
        emojiStats.forceStatCalculations();

        // make sure we found two emojis, and that the thumbs up emoji is ranked first and the second emoji
        // is ranked second
        if (emojiStats.getPercentageOfTweetsThatContainEmojis() != 0.5) {
            throw new Exception("Tweets with emoji's percentage is incorrect");
        }
    }

    private Status mockStatus(String text) {
        Status status = new Status() {
            @Override
            public Date getCreatedAt() {
                return null;
            }

            @Override
            public long getId() {
                return 0;
            }

            @Override
            public String getText() {
                return text;
            }

            @Override
            public String getSource() {
                return null;
            }

            @Override
            public boolean isTruncated() {
                return false;
            }

            @Override
            public long getInReplyToStatusId() {
                return 0;
            }

            @Override
            public long getInReplyToUserId() {
                return 0;
            }

            @Override
            public String getInReplyToScreenName() {
                return null;
            }

            @Override
            public GeoLocation getGeoLocation() {
                return null;
            }

            @Override
            public Place getPlace() {
                return null;
            }

            @Override
            public boolean isFavorited() {
                return false;
            }

            @Override
            public boolean isRetweeted() {
                return false;
            }

            @Override
            public int getFavoriteCount() {
                return 0;
            }

            @Override
            public User getUser() {
                return null;
            }

            @Override
            public boolean isRetweet() {
                return false;
            }

            @Override
            public Status getRetweetedStatus() {
                return null;
            }

            @Override
            public long[] getContributors() {
                return new long[0];
            }

            @Override
            public int getRetweetCount() {
                return 0;
            }

            @Override
            public boolean isRetweetedByMe() {
                return false;
            }

            @Override
            public long getCurrentUserRetweetId() {
                return 0;
            }

            @Override
            public boolean isPossiblySensitive() {
                return false;
            }

            @Override
            public String getLang() {
                return null;
            }

            @Override
            public Scopes getScopes() {
                return null;
            }

            @Override
            public int compareTo(Status o) {
                return 0;
            }

            @Override
            public UserMentionEntity[] getUserMentionEntities() {
                return new UserMentionEntity[0];
            }

            @Override
            public URLEntity[] getURLEntities() {
                return new URLEntity[0];
            }

            @Override
            public HashtagEntity[] getHashtagEntities() {
                return new HashtagEntity[0];
            }

            @Override
            public MediaEntity[] getMediaEntities() {
                return new MediaEntity[0];
            }

            @Override
            public SymbolEntity[] getSymbolEntities() {
                return new SymbolEntity[0];
            }

            @Override
            public RateLimitStatus getRateLimitStatus() {
                return null;
            }

            @Override
            public int getAccessLevel() {
                return 0;
            }
        };

        return status;
    }
}
