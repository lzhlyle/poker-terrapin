package com.lzhlyle.poker.terrapin.domain;

import com.lzhlyle.poker.terrapin.domain.game.AbstractPlayer;
import com.lzhlyle.poker.terrapin.domain.game.Banker;
import com.lzhlyle.poker.terrapin.domain.game.Player;
import com.lzhlyle.poker.core.card.PokerBox;
import com.lzhlyle.poker.core.card.PokerCard;
import com.lzhlyle.poker.core.dealer.PokerDealer;
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