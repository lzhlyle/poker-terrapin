package com.lzhlyle.poker.terrapin.domain.score;

import java.util.concurrent.atomic.AtomicInteger;

public class Score {
    private int value;
    private int curr;
    private int[] memo;

    public Score(int def) {
        this.value = def;
        this.curr = 0;
        memo = new int[2];
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

    public void clear() {
        memo[0] = this.value;
        memo[1] = this.curr;
        this.value = 0;
        this.curr = 0;
    }

    public void unClear() {
        this.value = memo[0];
        this.curr = memo[1];
    }
}
