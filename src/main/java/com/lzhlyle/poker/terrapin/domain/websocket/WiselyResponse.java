package com.lzhlyle.poker.terrapin.domain.websocket;

import com.lzhlyle.poker.terrapin.domain.game.AbstractPlayer;
import com.lzhlyle.poker.terrapin.domain.game.Banker;
import com.lzhlyle.poker.terrapin.domain.game.SimpleMall;

import java.util.List;

public class WiselyResponse {
    private String room;
    private boolean isChat;
    private String name;
    private String message;
    private Banker banker;
    private List<AbstractPlayer> players;

    public WiselyResponse(String room, String name, String message) {
        this(room, name, message, false);
    }

    public WiselyResponse(String room, String name, String message, boolean isChat) {
        this.room = room;
        this.name = name;
        this.message = message;
        this.banker = SimpleMall.getInstance().intoRoom(room).getBanker();
        this.players = SimpleMall.getInstance().intoRoom(room).getAbstractPlayers();
        this.isChat = isChat;
    }

    public String getRoom() {
        return room;
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

    public boolean isChat() {
        return isChat;
    }
}
