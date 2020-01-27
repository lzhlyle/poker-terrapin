package com.lzhlyle.poker.terrapin.domain;

import com.lzhlyle.poker.utility.card.PokerBox;
import com.lzhlyle.poker.utility.card.PokerCard;
import com.lzhlyle.poker.utility.dealer.PokerDealer;
import org.junit.Test;

import java.util.List;

public class AbstractPlayerTest {

    @Test
    public void receive() {
        List<PokerCard> cards = new PokerBox().getCards();
        PokerDealer dealer = PokerDealer.getInstance();
        dealer.shuffle(cards);

        AbstractPlayer abstractPlayer = new Player("MY");
        abstractPlayer.receive(dealer.deal(cards, 4));
    }

    @Test
    public void adjust() {
        List<PokerCard> cards = new PokerBox().getCards();
        PokerDealer dealer = PokerDealer.getInstance();
        dealer.shuffle(cards);

        AbstractPlayer abstractPlayer = new Banker("SC");
        abstractPlayer.receive(dealer.deal(cards, 4));
        System.out.println(abstractPlayer.getHandCard());

        abstractPlayer.adjust();
        System.out.println(abstractPlayer.getHandCard());

        abstractPlayer.adjust();
        System.out.println(abstractPlayer.getHandCard());

        abstractPlayer.adjust();
        System.out.println(abstractPlayer.getHandCard());
    }
}