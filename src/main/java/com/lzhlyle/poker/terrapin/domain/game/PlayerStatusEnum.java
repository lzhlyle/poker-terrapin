package com.lzhlyle.poker.terrapin.domain.game;

public enum PlayerStatusEnum {
    WAITING,
    LOSE,
    WIN,

    PLAYER_PASS,
    PLAYER_PASS_BANKER_PASS,
    PLAYER_PASS_BANKER_PASS_PLAYER_BELIEVE,
    PLAYER_PASS_BANKER_PASS_PLAYER_NOT_BELIEVE,
    PLAYER_PASS_BANKER_KILL,
    PLAYER_PASS_BANKER_KILL_PLAYER_BELIEVE,
    PLAYER_PASS_BANKER_KILL_PLAYER_NOT_BELIEVE,

    PLAYER_COVER,
    PLAYER_COVER_BANKER_NOT_TURN_OVER,
    PLAYER_COVER_BANKER_TURN_OVER,

    PLAYER_FORCE,
}
