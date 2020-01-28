package com.lzhlyle.poker.terrapin.domain.game;

import com.lzhlyle.poker.utility.card.PokerCard;

import java.util.List;

public class HandCardCollection {
    private int indexWithFirst;
    private PairCard head, tail;
    private List<PokerCard> cards;
    private boolean isLock;

    public HandCardCollection(List<PokerCard> cards) {
        if (cards == null || cards.size() != 4) throw new IllegalArgumentException();
        this.cards = cards;
        this.indexWithFirst = 1;
        this.isLock = false;
        PairCard p1 = new PairCard(cards.get(0), cards.get(1));
        PairCard p2 = new PairCard(cards.get(2), cards.get(3));

        refreshHeadAndTail(p1, p2);
    }

    private void refreshHeadAndTail(PairCard p1, PairCard p2) {
        if (p1.compareTo(p2) > 0) {
            head = p1;
            tail = p2;
            return;
        }

        head = p2;
        tail = p1;
    }

    public void adjust() {
        if (isLock) return;
        int i = indexWithFirst;
        this._adjust(i == 3 ? 1 : i + 1);
    }

    private void _adjust(int indexWithFirst) {
        if (this.indexWithFirst == indexWithFirst) return;
        this.indexWithFirst = indexWithFirst;
        PairCard p1 = new PairCard(cards.get(0), cards.get(indexWithFirst));
        PairCard p2;
        if (indexWithFirst == 1) {
            p2 = new PairCard(cards.get(2), cards.get(3));
        } else if (indexWithFirst == 2) {
            p2 = new PairCard(cards.get(1), cards.get(3));
        } else if (indexWithFirst == 3) {
            p2 = new PairCard(cards.get(1), cards.get(2));
        } else throw new IllegalArgumentException();

        refreshHeadAndTail(p1, p2);
    }

    public void lock() {
        isLock = true;
    }

    @Override
    public String toString() {
        return "{" + head + ", " + tail + '}';
    }
}
