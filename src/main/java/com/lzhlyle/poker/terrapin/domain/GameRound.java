package com.lzhlyle.poker.terrapin.domain;

import com.lzhlyle.poker.utility.dealer.PokerDealer;

public class GameRound {
    private Game game;
    private PokerDealer dealer;
    private Banker banker;
    private Player[] players;

    public GameRound(Game game) {
        this.game = game;
        this.dealer = PokerDealer.getInstance();
        this.banker = game.getBanker();
        this.players = game.getPlayers().toArray(new Player[0]);

        _init();
    }

    private void _init() {
        dealer.shuffle(game.getPokerCards());
    }

    public void start() {
        banker.receive(dealer.deal(game.getPokerCards(), 4));
        for (Player player : players) {
            player.receive(dealer.deal(game.getPokerCards(), 4));
        }
    }
}
