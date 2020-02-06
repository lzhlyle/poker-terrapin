package com.lzhlyle.poker.terrapin.domain.game;

import com.lzhlyle.poker.terrapin.domain.score.Score;
import com.lzhlyle.poker.core.card.PokerCard;

import java.util.List;

public abstract class AbstractPlayer {
    private String name;
    private HandCardCollection handCard;
    private Game game;
    private Score score;

    public AbstractPlayer(String name) {
        this.name = name;
        this.handCard = null;
        this.game = null;
        this.score = new Score(0);
    }

    public AbstractPlayer(String name, int scoreVal) {
        this.name = name;
        this.handCard = null;
        this.game = null;
        this.score = new Score(scoreVal);
    }

    // 接牌
    public void receive(List<PokerCard> cards) {
        this.handCard = new HandCardCollection(cards);
        this.score.clearCurr();
    }

    // 摆牌
    public boolean adjust() {
        if (handCard == null) return false;
        if (!handCard.isLock()) return handCard.adjust();
        return false;
    }

    // 加分
    public void increaseScore(int val) {
        score.calcValue(val);
    }

    // 减分
    public void decreaseScore(int val) {
        score.calcValue(val * -1);
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

    public Score getScore() {
        return score;
    }
}
