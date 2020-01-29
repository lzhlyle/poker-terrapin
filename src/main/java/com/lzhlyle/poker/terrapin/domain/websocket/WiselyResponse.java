package com.lzhlyle.poker.terrapin.domain.websocket;

import com.lzhlyle.poker.terrapin.domain.game.AbstractPlayer;
import com.lzhlyle.poker.terrapin.domain.game.Banker;
import com.lzhlyle.poker.terrapin.domain.game.SimpleGame;

import java.util.List;

public class WiselyResponse {
    private String name;
    private String message;
    private Banker banker;
    private List<AbstractPlayer> players;

    public WiselyResponse(String name, String message) {
        this.name = name;
        this.message = message;
        this.banker = SimpleGame.getInstance().getBanker();
        this.players = SimpleGame.getInstance().getAbstractPlayers();
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public List<AbstractPlayer> getPlayers() {
        return players;
    }

    public Banker getBanker() {
        return banker;
    }
}
