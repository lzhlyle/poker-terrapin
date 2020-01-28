package com.lzhlyle.poker.terrapin.utility;

public class TerrapinException {
    private String messsage;

    public TerrapinException(String message) {
        this.messsage = "[错误] " + message;
    }

    public String getMesssage() {
        return messsage;
    }
}
