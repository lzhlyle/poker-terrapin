package com.lzhlyle.poker.terrapin.domain;

import com.lzhlyle.poker.utility.card.PokerCard;

import java.util.List;

public class Player {
    private String name;
    private int sort;
    private HandCardCollection handCard;

    public Player(String name, int sort) {
        this.name = name;
        this.sort = sort;
        this.handCard = null;
    }

    public void receive(List<PokerCard> cards) {
        this.handCard = new HandCardCollection(cards);
    }

    public void adjust() {
        int i = handCard.getIndexWithFirst();
        handCard.adjust(i == 3 ? 1 : i + 1);
    }

    public String getName() {
        return name;
    }

    public int getSort() {
        return sort;
    }

    public HandCardCollection getHandCard() {
        return handCard;
    }
}
