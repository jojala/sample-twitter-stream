package com.jeffreyojala.twitterstream.processor;

import com.jeffreyojala.twitterstream.models.HashTag;
import com.jeffreyojala.twitterstream.models.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class HashTagTests {

    @Autowired
    private HashTagStats hashTagStats;

    @BeforeEach
    void clearStats() {
        hashTagStats.reset();
    }

    // Top hashtags
    @Test
    void testTopHashTags() throws Exception {

        String dadLife = "#DadLife";
        String highboyf250 = "#highboyf250";
        String theoutdoors = "#theoutdoors";

        Status tweet1 = mockStatus(Arrays.asList(dadLife, highboyf250)); // 1 dadlife, 1 highboyf250
        Status tweet2 = mockStatus(Arrays.asList(theoutdoors)); // 1 dadlife, 1 highboyf250, 1 theoutdoors
        Status tweet3 = mockStatus(Arrays.asList(theoutdoors,dadLife)); // 2 dadlife, 1 highboyf250, 2 theoutdoors
        Status tweet4 = mockStatus(Arrays.asList(theoutdoors)); // 2 dadlife, 1 highboyf250, 3 theoutdoors
        Status tweet5 = mockStatus(Arrays.asList(theoutdoors)); // 2 dadlife, 1 highboyf250, 4 theoutdoors

        hashTagStats.process(tweet1);
        hashTagStats.process(tweet2);
        hashTagStats.process(tweet3);
        hashTagStats.process(tweet4);
        hashTagStats.process(tweet5);

        // force our stat calculations
        hashTagStats.forceStatCalculations();

        Optional<Rank<HashTag>> topHashTag = hashTagStats.getHashtagByRank(1);
        Rank<HashTag> optval = topHashTag.get();
        HashTag val = optval.getValue();
        String tag = val.getHashTag();
        if (!topHashTag.isPresent() || topHashTag.get().getValue().getHashTag().compareTo(theoutdoors) != 0) {
            throw new Exception("#thedoors is not ranked first");
        }

        Optional<Rank<HashTag>> secondHashTag = hashTagStats.getHashtagByRank(2);
        if (!secondHashTag.isPresent() || secondHashTag.get().getValue().getHashTag().compareTo(dadLife) != 0) {
            throw new Exception("#dadlife is not ranked second");
        }

        Optional<Rank<HashTag>> thirdHashTag = hashTagStats.getHashtagByRank(3);
        if (!thirdHashTag.isPresent() || thirdHashTag.get().getValue().getHashTag().compareTo(highboyf250) != 0) {
            throw new Exception("#highboyf250 is not ranked third");
        }
    }

    private Status mockStatus(List<String> hashTagTexts) {
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
                return "";
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

                HashtagEntity[] entities = new HashtagEntity[hashTagTexts.size()];

                AtomicInteger count = new AtomicInteger(0);
                hashTagTexts.forEach(text ->{
                    HashtagEntity entity = new HashtagEntity() {
                        @Override
                        public String getText() {
                            return text;
                        }

                        @Override
                        public int getStart() {
                            return 0;
                        }

                        @Override
                        public int getEnd() {
                            return 0;
                        }
                    };
                    entities[count.getAndIncrement()] = entity;
                });


                return entities;
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
