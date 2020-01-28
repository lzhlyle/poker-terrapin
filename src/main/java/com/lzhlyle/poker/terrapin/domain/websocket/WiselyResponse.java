package com.lzhlyle.poker.terrapin.domain.websocket;

import com.lzhlyle.poker.terrapin.domain.game.Player;
import com.lzhlyle.poker.terrapin.domain.game.SimpleGame;

import java.util.List;

public class WiselyResponse {
    private String message;
    private List<Player> players;

    public WiselyResponse(String message) {
        this.message = message;
        this.players = SimpleGame.getInstance().getPlayers();
    }

    public String getMessage() {
        return message;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
