package com.lzhlyle.poker.terrapin.domain;

import com.lzhlyle.poker.terrapin.domain.game.HandCardCollection;
import com.lzhlyle.poker.utility.card.PokerBox;
import com.lzhlyle.poker.utility.card.PokerCard;
import com.lzhlyle.poker.utility.dealer.PokerDealer;
import org.junit.Test;

import java.util.List;

public class HandCardCollectionTest {

    @Test
    public void toStringTest() {
        List<PokerCard> cards = new PokerBox().getCards();

        PokerDealer dealer = PokerDealer.getInstance();
        dealer.shuffle(cards);

        HandCardCollection handCardCollection = new HandCardCollection(dealer.deal(cards, 4));
        System.out.println(handCardCollection.toString());
    }

    @Test
    public void adjust() {
        List<PokerCard> cards = new PokerBox().getCards();

        PokerDealer dealer = PokerDealer.getInstance();
        dealer.shuffle(cards);

        HandCardCollection handCardCollection = new HandCardCollection(dealer.deal(cards, 4));
        System.out.println(handCardCollection.toString());

        handCardCollection.adjust();
        System.out.println(handCardCollection.toString());
    }
}