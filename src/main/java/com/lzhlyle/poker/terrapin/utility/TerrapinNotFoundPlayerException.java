package com.lzhlyle.poker.terrapin.utility;

public class TerrapinNotFoundPlayerException extends TerrapinException {
    public TerrapinNotFoundPlayerException(String name) {
        super("找不到玩家 " + name);
    }
}
