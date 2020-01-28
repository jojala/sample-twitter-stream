package com.jeffreyojala.twitterstream.models;

public class Emoji implements Rankable {

    private String unifiedCode;
    private String name;

    public String getUnifiedCode() {
        return unifiedCode;
    }

    public void setUnifiedCode(String unifiedCode) {
        this.unifiedCode = unifiedCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getRankKey() {
        return unifiedCode;
    }
}
