package com.lzhlyle.poker.terrapin.domain.game;

import com.lzhlyle.poker.core.card.PokerCard;

import java.util.List;

public class Player extends AbstractPlayer {
    private PlayerStatusEnum status;

    public Player(String name) {
        super(name);
        this.status = PlayerStatusEnum.WAITING;
    }

    public Player(String name, int scoreVal, HandCardCollection oriHandCardCollection, boolean oriKing) {
        super(name, scoreVal, oriHandCardCollection, oriKing);
        this.status = PlayerStatusEnum.WAITING;
    }

    @Override
    public void receive(List<PokerCard> cards) {
        super.receive(cards);
        this.status = PlayerStatusEnum.WAITING;
    }

    public void sitDown(Game game) {
        game.addPlayer(this);
        super.setGame(game);
    }

    public void leave() {
        super.getGame().removePlayer(this);
        super.setGame(null);
    }

    // 求走
    public boolean pass() {
        if (this.status != PlayerStatusEnum.WAITING || super.getHandCard() == null) return false;
        super.getHandCard().lock();
        this.status = PlayerStatusEnum.PLAYER_PASS;
        return true;
    }

    public boolean bankerPass() {
        if (this.status != PlayerStatusEnum.PLAYER_PASS) return false;
        this.status = PlayerStatusEnum.PLAYER_PASS_BANKER_PASS;
        return true;
    }

    public boolean bankerPassBelieve() {
        if (this.status != PlayerStatusEnum.PLAYER_PASS_BANKER_PASS) return false;
        this.status = PlayerStatusEnum.PLAYER_PASS_BANKER_PASS_PLAYER_BELIEVE;
        return true;
    }

    public boolean bankerPassNotBelieve() {
        if (this.status != PlayerStatusEnum.PLAYER_PASS_BANKER_PASS) return false;
        this.status = PlayerStatusEnum.PLAYER_PASS_BANKER_PASS_PLAYER_NOT_BELIEVE;
        return true;
    }

    public boolean bankerKill() {
        if (this.status != PlayerStatusEnum.PLAYER_PASS) return false;
        this.status = PlayerStatusEnum.PLAYER_PASS_BANKER_KILL;
        return true;
    }

    public boolean bankerKillBelieve() {
        if (this.status != PlayerStatusEnum.PLAYER_PASS_BANKER_KILL) return false;
        this.status = PlayerStatusEnum.PLAYER_PASS_BANKER_KILL_PLAYER_BELIEVE;
        return true;
    }

    public boolean bankerKillNotBelieve() {
        if (this.status != PlayerStatusEnum.PLAYER_PASS_BANKER_KILL) return false;
        this.status = PlayerStatusEnum.PLAYER_PASS_BANKER_KILL_PLAYER_NOT_BELIEVE;
        return true;
    }

    // 盖牌
    public boolean cover() {
        if (this.status != PlayerStatusEnum.WAITING || super.getHandCard() == null) return false;
        super.getHandCard().lock();
        this.status = PlayerStatusEnum.PLAYER_COVER;
        return true;
    }

    public boolean bankerTurnOver() {
        if (this.status != PlayerStatusEnum.PLAYER_COVER) return false;
        this.status = PlayerStatusEnum.PLAYER_COVER_BANKER_TURN_OVER;
        return true;
    }

    public boolean bankerNotTurnOver() {
        if (this.status != PlayerStatusEnum.PLAYER_COVER) return false;
        this.status = PlayerStatusEnum.PLAYER_COVER_BANKER_NOT_TURN_OVER;
        return true;
    }

    // 强攻
    public boolean force() {
        if (this.status != PlayerStatusEnum.WAITING || super.getHandCard() == null) return false;
        super.getHandCard().lock();
        this.status = PlayerStatusEnum.PLAYER_FORCE;
        return true;
    }

    public PlayerStatusEnum getStatus() {
        return status;
    }
}
