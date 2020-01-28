package com.lzhlyle.poker.terrapin.domain.game;

public class Player extends AbstractPlayer {
    public Player(String name) {
        super(name);
    }

    public void sitDown(Game game) {
        game.addPlayer(this);
        super.setGame(game);
    }

    public void leave() {
        super.getGame().removePlayer(this);
        super.setGame(null);
    }

    // 开牌
    public String turnOver() {
        super.getHandCard().lock();
        return super.getHandCard().toString();
    }

    // 盖牌
    public void turnCover() {
        super.getHandCard().lock();
    }
}
