package com.lzhlyle.poker.terrapin.domain;

import com.lzhlyle.poker.utility.card.PokerBox;
import com.lzhlyle.poker.utility.card.PokerCard;
import com.lzhlyle.poker.utility.dealer.PokerDealer;
import org.junit.Test;

import java.util.List;

public class PlayerTest {

    @Test
    public void receive() {
        List<PokerCard> cards = new PokerBox().getCards();
        PokerDealer dealer = PokerDealer.getInstance();
        dealer.shuffle(cards);

        Player player = new Player("MY", 1);
        player.receive(dealer.deal(cards, 4));
    }

    @Test
    public void adjust() {
        List<PokerCard> cards = new PokerBox().getCards();
        PokerDealer dealer = PokerDealer.getInstance();
        dealer.shuffle(cards);

        Player player = new Player("SC", 2);
        player.receive(dealer.deal(cards, 4));
        System.out.println(player.getHandCard());

        player.adjust();
        System.out.println(player.getHandCard());

        player.adjust();
        System.out.println(player.getHandCard());

        player.adjust();
        System.out.println(player.getHandCard());
    }
}