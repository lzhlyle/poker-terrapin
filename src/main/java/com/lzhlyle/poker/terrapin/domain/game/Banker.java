package com.lzhlyle.poker.terrapin.domain.game;

public class Banker extends AbstractPlayer {
    public Banker(String name) {
        super(name);
        super.setGame(new Game(this));
    }

    public void round() {
        new GameRound(super.getGame()).start();
    }

    // 杀
    public void kill() {

    }

    // 走过
    public void pass() {

    }
}
