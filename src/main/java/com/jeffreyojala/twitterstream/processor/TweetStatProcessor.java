package com.jeffreyojala.twitterstream.processor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.Status;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Main container class for processing status updates
 */
@Component
public class TweetStatProcessor {

    @Autowired
    private EmojiStats emojiStats;

    @Autowired
    private HashTagStats hashTagStats;

    @Autowired
    private UrlStats urlStats;

    @Autowired
    private ReceivedStats receivedStats;

    private List<StatStep> processSteps = new ArrayList<>();

    @PostConstruct
    public void CollectProcessors() {
        processSteps.add(emojiStats);
        processSteps.add(hashTagStats);
        processSteps.add(urlStats);
        processSteps.add(receivedStats);
    }


    /*
        Total number of tweets received (count)
        Average tweets per hour/minute/second
        Top emojis in tweets (count)
        Percent of tweets that contains emojis (count)
        Top hashtags (count)
        Percent of tweets that contain a url
        Percent of tweets that contain a photo url (pic.twitter.com, pbs.twimg.com, or instagram)
        Top domains of urls in tweets
    */


    public void classify(Status status) {

        // run our status object through each of our processing steps
        processSteps.forEach(step->{
            step.process(status);
        });
    }

}
