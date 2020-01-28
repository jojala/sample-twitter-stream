package com.jeffreyojala.twitterstream.processor;

import com.jeffreyojala.twitterstream.processor.UrlStats;
import org.junit.jupiter.api.BeforeAll;
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
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Percent of tweets that contain a url
 * Percent of tweets that contain a photo url (pic.twitter.com, pbs.twimg.com, or instagram)
 * Top domains of urls in tweets
 */
@SpringBootTest
public class UrlTests {

    @Autowired
    private UrlStats urlStats;

    @BeforeEach
    void clearStats() {
        urlStats.reset();
    }

    private void processTestTweets() {
        Status tweet1 = mockStatus(Arrays.asList("https://pic.twitter.com/img.png", "https://www.google.com"));
        Status tweet2 = mockStatus(Arrays.asList("https://pbs.twimg.com/img.png"));
        Status tweet3 = mockStatus(Arrays.asList("https://instagram.com/img.png"));
        Status tweet4 = mockStatus(Arrays.asList("https://www.google.com"));
        Status tweet5 = mockStatus(Arrays.asList());

        urlStats.process(tweet1);
        urlStats.process(tweet2);
        urlStats.process(tweet3);
        urlStats.process(tweet4);
        urlStats.process(tweet5);
    }
    @Test
    void testPercentOfTweetsThatContainAUrl() throws Exception {
        processTestTweets();

        // force our stat calculations
        urlStats.forceStatCalculations();

        if (urlStats.getPercentageOfTweetsThatContainUrls() != 0.8) {
            throw new Exception("tweets that contain URL total count is wrong");
        }
    }

    @Test
    void testPercentOfTweetsThatContainPhotoUrls() throws Exception {
        processTestTweets();

        // force our stat calculations
        urlStats.forceStatCalculations();

        if (urlStats.getPercentageOfTweetsThatContainPhotoUrls() != 0.6) {
            throw new Exception("tweets that contain photo URL total count is wrong");
        }
    }

    @Test
    void testTopDomains() throws Exception {

        processTestTweets();

        // force our stat calculations
        urlStats.forceStatCalculations();

        if (urlStats.getDomainByRank(1).get().getValue().getDomain().compareTo("www.google.com") != 0) {
            throw new Exception("top domains for tweets is wrong");
        }
    }

    private Status mockStatus(List<String> urls) {
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
                URLEntity[] urlEntities = new URLEntity[urls.size()];
                AtomicInteger pos = new AtomicInteger(0);
                urls.forEach( url -> {
                    URLEntity entity = new URLEntity() {
                        @Override
                        public String getText() {
                            return null;
                        }

                        @Override
                        public String getURL() {
                            return url;
                        }

                        @Override
                        public String getExpandedURL() {
                            return url;
                        }

                        @Override
                        public String getDisplayURL() {
                            return url;
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
                    urlEntities[pos.getAndIncrement()] = entity;
                });

                return urlEntities;
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
