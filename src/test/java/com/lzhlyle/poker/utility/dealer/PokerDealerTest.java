package com.lzhlyle.poker.utility.dealer;

import com.lzhlyle.poker.utility.card.PokerBox;
import com.lzhlyle.poker.utility.card.PokerCard;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class PokerDealerTest {

    @Test
    public void shuffle_whole_box() {
        List<PokerCard> cards = new PokerBox().getCards();

        System.out.println(Arrays.toString(cards.toArray()));

        PokerDealer.getInstance().shuffle(cards);
        System.out.println("after shuffling the whole box: " + Arrays.toString(cards.toArray()));
    }

    @Test
    public void shuffle_a_part_of_box() {
        List<PokerCard> cards = new PokerBox().getCards();

        System.out.println(Arrays.toString(cards.toArray()));

        PokerDealer.getInstance().shuffle(cards.subList(10, 30));
        System.out.println("after shuffling a part of box: " + Arrays.toString(cards.toArray()));
    }

    @Test
    public void deal_should_success() {
        List<PokerCard> cards = new PokerBox().getCards();
        Assert.assertEquals(54, cards.size());

        PokerDealer dealer = PokerDealer.getInstance();

        int n = 4;
        List<PokerCard> myCards = dealer.deal(cards, n);
        System.out.println(Arrays.toString(myCards.toArray()));
        Assert.assertEquals(54 - n, cards.size());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void deal_should_out_of_range() {
        PokerDealer.getInstance().deal(new PokerBox().getCards(), 55);
    }
}