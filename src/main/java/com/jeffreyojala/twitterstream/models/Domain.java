package com.jeffreyojala.twitterstream.models;

public class Domain implements Rankable {

    private String domain;

    public Domain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getRankKey() {
        return domain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
