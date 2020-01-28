package com.jeffreyojala.twitterstream.processor;

import twitter4j.Status;


public interface StatStep {
    void process(Status status);
    void reset();
}
