package com.lzhlyle.poker.terrapin.domain.game;

import com.lzhlyle.poker.core.card.PokerBox;
import com.lzhlyle.poker.core.card.PokerCard;
import com.lzhlyle.poker.core.dealer.PokerDealer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SimpleGame {
    private Banker banker;
    private List<Player> players;
    private List<Observer> observers;
    private PokerDealer dealer;
    private final String code;

    public SimpleGame(String code) {
        this.players = new LinkedList<>();
        this.observers = new LinkedList<>();
        this.dealer = PokerDealer.getInstance();
        this.code = code;
    }

    public void start(String name) {
        List<PokerCard> pokerCards = new PokerBox().getCards();
        dealer.shuffle(pokerCards);


        if (banker != null && !Objects.equals(banker.getName(), name)) {
            // banker -> player
            String oriBankerName = banker.getName();
            int oriScoreVal = banker.getScore().getValue();
            boolean oriKing = banker.getKing();
            HandCardCollection oriHandCardCollection = banker.getHandCard();
            banker = null;
            addPlayer(oriBankerName, oriScoreVal, oriHandCardCollection, oriKing);
        }

        if (banker == null) {
            // player -> banker
            Player oriPlayer = findPlayer(name);
            boolean oriKing = oriPlayer.getKing();
            HandCardCollection oriHandCardCollection = oriPlayer.getHandCard();
            AbstractPlayer ab = removePlayer(name);
            banker = new Banker(name, ab.getScore().getValue(), oriHandCardCollection, oriKing);
        }

        List<AbstractPlayer> abstractPlayers = getAbstractPlayers();
        boolean refreshKing = anyNewKing(abstractPlayers);
        for (AbstractPlayer player : abstractPlayers) {
            if (refreshKing) player.refreshKing();
            player.receive(dealer.deal(pokerCards, 4));
        }
    }

    public List<AbstractPlayer> getAbstractPlayers() {
        List<AbstractPlayer> res = new ArrayList<>(players.size() + 1);
        res.addAll(players);
        if (banker != null) res.add(banker);
        return res;
    }

    public boolean addPlayer(String name) {
        return addPlayer(name, 0);
    }

    public boolean addPlayer(String name, int scoreVal) {
        return addPlayer(name, scoreVal, null, false);
    }

    public boolean addPlayer(String name, int scoreVal, HandCardCollection oriHandCardCollection, boolean oriKing) {
        if (getAbstractPlayers().stream().anyMatch(p -> Objects.equals(p.getName(), name))) return false;
        players.add(new Player(name, scoreVal, oriHandCardCollection, oriKing));
        return true;
    }

    public boolean addObserver(String name) {
        if (observers.stream().anyMatch(ob -> Objects.equals(ob.getName(), name))) return false;
        observers.add(new Observer(name));
        return true;
    }

    public boolean changeToObserver(String name) {
        AbstractPlayer ap = findAbstractPlayer(name);
        if (ap == null) return false;

        if (addObserver(name)) {
            removePlayer(name);
            return true;
        }
        return false;
    }

    public boolean changeToPlayer(String name) {
        Observer ob = findObserver(name);
        if (ob == null) return false;

        AbstractPlayer original = ob.getOriginal();
        if (original == null) {
            if (addPlayer(name)) {
                removeObserver(name);
                return true;
            }
            return false;
        }

        if (addPlayer(original.getName(), original.getScore().getValue())) {
            removeObserver(name);
            return true;
        }
        return false;
    }

    public AbstractPlayer removePlayer(String name) {
        AbstractPlayer player = findAbstractPlayer(name);
        if (player instanceof Player) players.remove(player);
        if (player instanceof Banker) banker = null;
        return player;
    }

    public boolean removeObserver(String name) {
        return observers.removeIf(ob -> Objects.equals(ob.getName(), name));
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

    public Observer findObserver(String name) {
        return observers.stream().filter(ob -> Objects.equals(ob.getName(), name)).findFirst().orElse(null);
    }

    public Banker getBanker() {
        return banker;
    }

    public String getCode() {
        return code;
    }

    public List<Observer> getObservers() {
        return observers;
    }

    private boolean anyNewKing(List<AbstractPlayer> abstractPlayers) {
        for (AbstractPlayer p : abstractPlayers) {
            if (p.getHandCard() != null && p.getHandCard().isFish()) return true;
        }
        return false;
    }
}
