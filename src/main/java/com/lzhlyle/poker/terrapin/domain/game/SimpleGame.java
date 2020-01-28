package com.lzhlyle.poker.terrapin.domain.game;

import com.lzhlyle.poker.utility.card.PokerBox;
import com.lzhlyle.poker.utility.card.PokerCard;
import com.lzhlyle.poker.utility.dealer.PokerDealer;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SimpleGame {
    private static SimpleGame ourInstance = new SimpleGame();

    public static SimpleGame getInstance() {
        return ourInstance;
    }

    private List<Player> players;
    private PokerDealer dealer;

    private SimpleGame() {
        this.players = new LinkedList<>();
        this.dealer = PokerDealer.getInstance();
    }

    public void start() {
        List<PokerCard> pokerCards = new PokerBox().getCards();
        dealer.shuffle(pokerCards);

        for (Player player : players) {
            player.receive(dealer.deal(pokerCards, 4));
        }
    }

    public void addPlayer(String name) {
        if (players.stream().anyMatch(p -> Objects.equals(p.getName(), name))) return;
        players.add(new Player(name));
    }

    public void removePlayer(String name) {
        Player player = findPlayer(name);
        players.remove(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player findPlayer(String name) {
        return players.stream().filter(p -> Objects.equals(p.getName(), name)).findFirst().orElse(null);
    }
}
