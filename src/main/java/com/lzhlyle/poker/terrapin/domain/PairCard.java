package com.lzhlyle.poker.terrapin.domain;

import com.lzhlyle.poker.utility.card.PokerCard;

public class PairCard {
    private PokerCard a;
    private PokerCard b;
    private int value;

    public PairCard(PokerCard a, PokerCard b) {
        this.a = a;
        this.b = b;
        this.value = calc(a, b);
    }

    private int calc(PokerCard a, PokerCard b) {
        // TODO lzh calculate pair's value
        return -1;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{" + a +
                ", " + b +
                ", v=" + value +
                '}';
    }
}
