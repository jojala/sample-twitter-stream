package com.jeffreyojala.twitterstream.models;

public class HashTag implements Rankable {

    private String hashTag;

    public HashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    @Override
    public String getRankKey() {
        return hashTag;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }
}
