package com.lzhlyle.poker.terrapin.domain.game;

import com.lzhlyle.poker.core.card.PokerCard;

import java.util.List;

public class Banker extends AbstractPlayer {
    private BankerStatusEnum status;
    private int count;

    public Banker(String name) {
        super(name);
        this.status = BankerStatusEnum.WAITING;
        this.count = 0;
    }

    public Banker(String name, int scoreVal) {
        super(name, scoreVal);
        this.status = BankerStatusEnum.WAITING;
        this.count = 0;
    }

    public Banker(String name, int scoreVal, HandCardCollection oriHandCardCollection, boolean oriKing) {
        super(name, scoreVal, oriHandCardCollection, oriKing);
        this.status = BankerStatusEnum.WAITING;
        this.count = 0;
    }

    public void round() {
        new GameRound(super.getGame()).start();
    }

    @Override
    public void receive(List<PokerCard> cards) {
        super.receive(cards);
        this.status = BankerStatusEnum.WAITING;
        this.count++;
    }

    // 走过
    public boolean pass(Player player) {
        if (super.getHandCard() == null) return false;
        super.getHandCard().lock();
        return player.bankerPass();
    }

    // 杀
    public boolean kill(Player player) {
        if (super.getHandCard() == null) return false;
        super.getHandCard().lock();
        return player.bankerKill();
    }

    // 开
    public boolean turnOver(Player player) {
        if (super.getHandCard() == null) return false;
        super.getHandCard().lock();
        return player.bankerTurnOver();
    }

    // 不开
    public boolean notTurnOver(Player player) {
        if (super.getHandCard() == null) return false;
        super.getHandCard().lock();
        return player.bankerNotTurnOver();
    }

    // 亮牌
    public boolean open() {
        if (this.status != BankerStatusEnum.WAITING || super.getHandCard() == null) return false;
        super.getHandCard().lock();
        this.status = BankerStatusEnum.OPEN;
        return true;
    }

    public BankerStatusEnum getStatus() {
        return status;
    }

    public int getCount() {
        return count;
    }
}
