package com.lzhlyle.poker.terrapin.domain.game;

public class Observer {
    private String name;
    private AbstractPlayer original;

    public Observer(String name) {
        this.name = name;
    }

    public Observer(AbstractPlayer original) {
        this(original.getName());
        this.original = original;
    }

    public String getName() {
        return name;
    }

    public AbstractPlayer getOriginal() {
        return original;
    }
}
