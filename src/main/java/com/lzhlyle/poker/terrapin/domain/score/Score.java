package com.lzhlyle.poker.terrapin.domain.score;

import java.util.concurrent.atomic.AtomicInteger;

public class Score {
    private int value;
    private int curr;

    public Score(int def) {
        this.value = def;
        this.curr = 0;
    }

    public int getValue() {
        return value;
    }

    public int getCurr() {
        return curr;
    }

    public void clearCurr() {
        this.curr = 0;
    }

    public void calcValue(int value) {
        this.value += value;
        this.curr += value;
    }
}
