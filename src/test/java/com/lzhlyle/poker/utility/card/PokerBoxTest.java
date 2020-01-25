package com.lzhlyle.poker.utility.card;

import org.junit.Test;

import java.util.Arrays;

public class PokerBoxTest {
    @Test
    public void getCards() {
        PokerCard[] cards = new PokerBox().getCards().toArray(new PokerCard[0]);
        System.out.println(Arrays.toString(cards));
        System.out.println(cards.length);
    }
}