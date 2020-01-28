package com.lzhlyle.poker.terrapin.domain.game;

import com.lzhlyle.poker.utility.card.PokerCard;

import java.util.List;

public abstract class AbstractPlayer {
    private String name;
    private HandCardCollection handCard;
    private Game game;

    public AbstractPlayer(String name) {
        this.name = name;
        this.handCard = null;
        this.game = null;
    }

    // 接牌
    public void receive(List<PokerCard> cards) {
        this.handCard = new HandCardCollection(cards);
    }

    // 摆牌
    public void adjust() {
        handCard.adjust();
    }

    public String getName() {
        return name;
    }

    public String getHandCardStr() {
        return handCard == null ? null : handCard.toString();
    }

    public HandCardCollection getHandCard() {
        return handCard;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
