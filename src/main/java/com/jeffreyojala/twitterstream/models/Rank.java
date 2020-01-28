package com.jeffreyojala.twitterstream.models;


public class Rank<T> {

    private T value;
    private int rank;
    private int count;

    public Rank(T value, int rank, int count) {
        this.value = value;
        this.rank = rank;
        this.count = count;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
