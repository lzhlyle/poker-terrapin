package com.lzhlyle.poker.terrapin.domain.game;

import com.lzhlyle.poker.utility.card.PokerBox;
import com.lzhlyle.poker.utility.card.PokerCard;
import com.lzhlyle.poker.utility.dealer.PokerDealer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SimpleGame {
    private static SimpleGame ourInstance = new SimpleGame();

    public static SimpleGame getInstance() {
        return ourInstance;
    }

    private Banker banker;
    private List<Player> players;
    private PokerDealer dealer;

    private SimpleGame() {
        this.players = new LinkedList<>();
        this.dealer = PokerDealer.getInstance();
    }

    public void start(String name) {
        List<PokerCard> pokerCards = new PokerBox().getCards();
        dealer.shuffle(pokerCards);

        if (banker != null && !Objects.equals(banker.getName(), name)) {
            // banker -> player
            String oriBankerName = banker.getName();
            int oriScoreVal = banker.getScore().getValue();
            banker = null;
            addPlayer(oriBankerName, oriScoreVal);
        }

        if (banker == null) {
            // player -> banker
            AbstractPlayer ab = removePlayer(name);
            banker = new Banker(name, ab.getScore().getValue());
        }

        List<AbstractPlayer> abstractPlayers = getAbstractPlayers();
        for (AbstractPlayer player : abstractPlayers) {
            player.receive(dealer.deal(pokerCards, 4));
        }
    }

    public List<AbstractPlayer> getAbstractPlayers() {
        List<AbstractPlayer> res = new ArrayList<>(players.size() + 1);
        res.addAll(players);
        if (banker != null) res.add(banker);
        return res;
    }

    public void addPlayer(String name) {
        addPlayer(name, 0);
    }

    public void addPlayer(String name, int scoreVal) {
        if (getAbstractPlayers().stream().anyMatch(p -> Objects.equals(p.getName(), name))) return;
        players.add(new Player(name, scoreVal));
    }

    public AbstractPlayer removePlayer(String name) {
        AbstractPlayer player = findAbstractPlayer(name);
        if (player instanceof Player) players.remove(player);
        if (player instanceof Banker) banker = null;
        return player;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public AbstractPlayer findAbstractPlayer(String name) {
        return getAbstractPlayers().stream().filter(p -> Objects.equals(p.getName(), name)).findFirst().orElse(null);
    }

    public Player findPlayer(String name) {
        return players.stream().filter(p -> Objects.equals(p.getName(), name)).findFirst().orElse(null);
    }

    public Banker findBanker(String name) {
        return Objects.equals(banker.getName(), name) ? banker : null;
    }

    public Banker getBanker() {
        return banker;
    }
}
