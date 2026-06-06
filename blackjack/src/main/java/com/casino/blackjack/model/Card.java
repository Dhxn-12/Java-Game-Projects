package com.casino.blackjack.model;

import java.io.Serializable;

/**
 * Represents a single playing card.
 * Immutable rank/suit; mutable face-up state.
 */
public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Suit {
        HEARTS("♥","Hearts"), DIAMONDS("♦","Diamonds"),
        CLUBS("♣","Clubs"),   SPADES("♠","Spades");
        private final String symbol, name;
        Suit(String s,String n){symbol=s;name=n;}
        public String getSymbol(){return symbol;}
        public String getName(){return name;}
    }

    public enum Rank {
        TWO("2",2),THREE("3",3),FOUR("4",4),FIVE("5",5),SIX("6",6),
        SEVEN("7",7),EIGHT("8",8),NINE("9",9),TEN("10",10),
        JACK("J",10),QUEEN("Q",10),KING("K",10),ACE("A",11);
        private final String symbol;
        private final int value;
        Rank(String s,int v){symbol=s;value=v;}
        public String getSymbol(){return symbol;}
        public int getValue(){return value;}
    }

    private final Suit suit;
    private final Rank rank;
    private boolean faceUp;

    public Card(Suit suit,Rank rank){this.suit=suit;this.rank=rank;this.faceUp=true;}

    public Suit getSuit(){return suit;}
    public Rank getRank(){return rank;}
    public boolean isFaceUp(){return faceUp;}
    public void setFaceUp(boolean b){faceUp=b;}
    public void flip(){faceUp=!faceUp;}
    public int getValue(){return rank.getValue();}
    public boolean isAce(){return rank==Rank.ACE;}
    public boolean isFaceCard(){return rank==Rank.JACK||rank==Rank.QUEEN||rank==Rank.KING;}
    public boolean isRedSuit(){return suit==Suit.HEARTS||suit==Suit.DIAMONDS;}

    public String getShortDisplay(){return faceUp?rank.getSymbol()+suit.getSymbol():"??";}
    public String getFullDisplay(){return faceUp?rank.getSymbol()+" of "+suit.getName():"Hidden Card";}
    public String getImageKey(){return rank.name()+"_"+suit.name();}

    @Override public String toString(){return getShortDisplay();}
    @Override public boolean equals(Object o){
        if(this==o)return true;
        if(!(o instanceof Card c))return false;
        return suit==c.suit&&rank==c.rank;
    }
    @Override public int hashCode(){return 31*suit.hashCode()+rank.hashCode();}
}
