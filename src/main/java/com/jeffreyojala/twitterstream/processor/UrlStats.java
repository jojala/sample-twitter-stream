package com.jeffreyojala.twitterstream.processor;

import com.jeffreyojala.twitterstream.models.Domain;
import com.jeffreyojala.twitterstream.models.Rank;
import com.jeffreyojala.twitterstream.statcontainers.Percent;
import com.jeffreyojala.twitterstream.statcontainers.Ranking;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.URLEntity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

/**
 * Percent of tweets that contain a url
 * Percent of tweets that contain a photo url (pic.twitter.com, pbs.twimg.com, or instagram)
 * Top domains of urls in tweets
 */
@Component
public class UrlStats implements StatStep {

    @Bean
    private Ranking<Domain> domainRanking() {
        return domainRanking;
    }
    private Ranking<Domain> domainRanking = new Ranking<Domain>();

    // Tweets that contains url
    private Percent totalTweetsWithUrls = new Percent();

    // Tweets that contain a photo url
    private Percent totalTweetsWithPhotoUrls = new Percent();

    public Optional<Rank<Domain>> getDomainByRank(int rank) {
        return domainRanking.getValueByRank(rank);
    }

    public double getPercentageOfTweetsThatContainUrls() {
        return totalTweetsWithUrls.getPercentage();
    }

    public double getPercentageOfTweetsThatContainPhotoUrls() {
        return totalTweetsWithPhotoUrls.getPercentage();
    }

    @Override
    public void process(Status status) {

        // determine if tweet has a url
        if (status.getURLEntities() != null && status.getURLEntities().length > 0) {
            totalTweetsWithUrls.increment(true);

            URLEntity[] list = status.getURLEntities();

            // rank domains
            Arrays.asList(list).stream()
                    .map(urlEntity -> {
                        URL url = null;
                        try {
                            url = new URL(urlEntity.getExpandedURL());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        String host = url.getHost();
                        domainRanking.countValue(new Domain(host));
                        return 1;
                    }).count();

            // determine if tweet has a url with a photo
            boolean tweetHasUrlWithPhoto = Arrays.asList(list).stream()
                    .map(urlEntity -> {
                        try {
                            URL url = new URL(urlEntity.getURL());
                            String host = url.getHost();
                            return host;
                        }
                        catch (MalformedURLException e) {
                            return "";
                        }
                    })
                    .filter(domain -> !domain.equals(""))
                    .anyMatch(domain -> (domain.contains("pic.twitter.com") ||
                            domain.contains("pbs.twimg.com") ||
                            domain.contains("instagram.com")));

            totalTweetsWithPhotoUrls.increment(tweetHasUrlWithPhoto);
        }
        else {
            totalTweetsWithUrls.increment(false);
            totalTweetsWithPhotoUrls.increment(false);
        }
    }

    @Override
    public void reset() {
        domainRanking.reset();
        totalTweetsWithUrls.reset();
        totalTweetsWithPhotoUrls.reset();
    }

    void forceStatCalculations() {
        domainRanking.rank();
    }
}
