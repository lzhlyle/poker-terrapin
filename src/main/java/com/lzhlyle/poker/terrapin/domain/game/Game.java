package com.lzhlyle.poker.terrapin.domain.game;

import com.lzhlyle.poker.core.card.PokerBox;
import com.lzhlyle.poker.core.card.PokerCard;

import java.util.LinkedList;
import java.util.List;

public class Game {
    private Banker banker;
    private List<Player> players;
    private List<PokerCard> pokerCards;

    public Game(Banker banker) {
        this.banker = banker;
        this.players = new LinkedList<>();
        this.pokerCards = new PokerBox().getCards();
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public Banker getBanker() {
        return banker;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<PokerCard> getPokerCards() {
        return pokerCards;
    }
}
