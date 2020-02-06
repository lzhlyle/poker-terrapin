package com.lzhlyle.poker.terrapin.domain.game;

import com.lzhlyle.poker.core.card.PokerCard;

import java.util.Objects;

public class PairCard {
    private PokerCard a;
    private PokerCard b;

    public PairCard(PokerCard a, PokerCard b) {
        this.a = a;
        this.b = b;
    }

    public int compareTo(PairCard pairCard) {
        return 0;

//        if (this.equals(pairCard)) return 0;
//
//        // 都纯对
//        if (this.a.getValue() == this.b.getValue() && pairCard.a.getValue() == pairCard.b.getValue()) {
//            // 比较各自的较大单牌
//            PokerCard thisBigger = this.a.compareTo(this.b) > 0 ? this.a : this.b;
//            PokerCard otherBigger = pairCard.a.compareTo(pairCard.b) > 0 ? pairCard.a : pairCard.b;
//            return thisBigger.compareTo(otherBigger);
//        }
//
//        // 一个对子，另一个非对子
//
//        // TODO lzh 存在王变牌
//
//        // 都非对子
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PairCard pairCard = (PairCard) o;
        return (a.equals(pairCard.a) && b.equals(pairCard.b))
                || (a.equals(pairCard.b) && b.equals(pairCard.a));
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        return "[" + a +
                ", " + b +
                ']';
    }

    public PokerCard getA() {
        return a;
    }

    public PokerCard getB() {
        return b;
    }
}
