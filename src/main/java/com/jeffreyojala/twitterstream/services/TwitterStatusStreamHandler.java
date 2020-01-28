package com.jeffreyojala.twitterstream.services;

import com.jeffreyojala.twitterstream.processor.TweetStatProcessor;
import com.twitter.hbc.twitter4j.handler.StatusStreamHandler;
import com.twitter.hbc.twitter4j.message.DisconnectMessage;
import com.twitter.hbc.twitter4j.message.StallWarningMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;


/**
 * Main handler for receive tweet status updates. Right now it
 * is basically just an entry point for receiving status updates
 * but there's a lot more "application" level control we could / should
 * do.
 * TODO: add in support for handling stalls, disconnects etc
 */
@Component
public class TwitterStatusStreamHandler implements StatusStreamHandler {

    @Autowired
    private TweetStatProcessor tweetStatProcessor;

    public TwitterStatusStreamHandler() {

    }

    @Override
    public void onStatus(Status status) {

        // lets classify our status
        tweetStatProcessor.classify(status);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        // For this exercise, we do not care about deletions
    }

    @Override
    public void onTrackLimitationNotice(int i) {
        //
    }

    @Override
    public void onScrubGeo(long l, long l1) {

    }

    @Override
    public void onStallWarning(StallWarning stallWarning) {

    }

    @Override
    public void onException(Exception e) {

    }

    @Override
    public void onDisconnectMessage(DisconnectMessage disconnectMessage) {

    }

    @Override
    public void onStallWarningMessage(StallWarningMessage stallWarningMessage) {

    }

    @Override
    public void onUnknownMessageType(String s) {

    }
}
