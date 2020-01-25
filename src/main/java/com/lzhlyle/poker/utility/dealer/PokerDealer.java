package com.lzhlyle.poker.utility.dealer;

import com.lzhlyle.poker.utility.card.PokerCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PokerDealer {
    private static PokerDealer ourInstance = new PokerDealer();

    public static PokerDealer getInstance() {
        return ourInstance;
    }

    private PokerDealer() {

    }

    // 洗牌
    public void shuffle(List<PokerCard> cards) {
        Collections.shuffle(cards);
    }

    // 发牌
    public List<PokerCard> deal(List<PokerCard> cards, int n) {
        if (n < 1 || n > cards.size()) throw new ArrayIndexOutOfBoundsException(n);
        List<PokerCard> res = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            res.add(cards.remove(0)); // always the top
        }
        return res;
    }
}
