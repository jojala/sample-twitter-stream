package com.jeffreyojala.twitterstream.models;

import java.util.HashMap;
import java.util.Map;

public class CollectedStats {

    //    Total number of tweets received
    public int totalNumberOfTweetsReceived;

    //    Average tweets per hour/minute/second
    public double currentTweetsPerSecond;
    public double currentTweetsPerMinute;
    public double currentTweetsPerHour;

    //    Top emojis in tweets
    public Map<Integer,Rank<Emoji>> topTenEmojis = new HashMap<>();

    //    Percent of tweets that contains emojis
    public double percentageOfTweetsThatContainEmojis;

    //    Top hashtags
    public Map<Integer,Rank<HashTag>> topTenHashTags = new HashMap<>();

    //    Percent of tweets that contain a url
    public double percentageOfTweetsThatContainAUrl;

    //    Percent of tweets that contain a photo url (pic.twitter.com, pbs.twimg.com, or instagram)
    public double percentageOfTweetsThatContainAPhotoUrl;

    //    Top domains of urls in tweets
    public Map<Integer,Rank<Domain>> topTenDomains = new HashMap<>();
}
