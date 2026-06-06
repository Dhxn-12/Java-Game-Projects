package com.casino.blackjack.model;

import java.io.Serializable;
import java.util.*;

/**
 * A shoe of 1–8 standard 52-card decks with Hi-Lo count support.
 */
public class Deck implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Card> cards    = new ArrayList<>();
    private final List<Card> discards = new ArrayList<>();
    private final int deckCount;
    private int cutCardPosition;

    public Deck(int deckCount){
        this.deckCount=Math.max(1,Math.min(8,deckCount));
        build(); shuffle();
    }

    private void build(){
        cards.clear(); discards.clear();
        for(int d=0;d<deckCount;d++)
            for(Card.Suit s:Card.Suit.values())
                for(Card.Rank r:Card.Rank.values())
                    cards.add(new Card(s,r));
        cutCardPosition=(int)(cards.size()*(0.60+Math.random()*0.15));
    }

    public void shuffle(){
        cards.addAll(discards); discards.clear();
        Collections.shuffle(cards);
        cutCardPosition=(int)(cards.size()*(0.60+Math.random()*0.15));
    }

    public Card draw(){if(cards.isEmpty())shuffle();return cards.remove(0);}
    public Card drawFaceDown(){Card c=draw();c.setFaceUp(false);return c;}
    public void discard(Card c){c.setFaceUp(true);discards.add(c);}
    public void discardAll(Hand h){for(Card c:h.getCards())discard(c);}

    public boolean needsReshuffle(){return cards.size()<=(totalCards()-cutCardPosition);}
    public int cardsRemaining(){return cards.size();}
    public int totalCards(){return deckCount*52;}
    public int getDeckCount(){return deckCount;}
    public double getPenetration(){return(double)discards.size()/totalCards()*100.0;}

    public int getHiLoRunningCount(){
        int count=0;
        for(Card c:discards){
            int v=c.getValue();
            if(v>=2&&v<=6)count++;
            else if(v==10||c.isAce())count--;
        }
        return count;
    }

    public double getTrueCount(){
        double dr=(double)cards.size()/52.0;
        return dr>0?getHiLoRunningCount()/dr:0;
    }

    @Override public String toString(){
        return String.format("Shoe[%d decks, %d remaining, %.0f%% penetration]",
                deckCount,cards.size(),getPenetration());
    }
}
