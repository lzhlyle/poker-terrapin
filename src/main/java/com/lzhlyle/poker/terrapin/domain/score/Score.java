package com.lzhlyle.poker.terrapin.domain.score;

public class Score {
    private int value;

    public Score(int def) {
        this.value = def;
    }

    public int getValue() {
        return value;
    }

    public void calcValue(int value) {
        this.value += value;
    }
}
