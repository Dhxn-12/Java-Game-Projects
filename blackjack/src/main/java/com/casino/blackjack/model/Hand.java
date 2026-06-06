package com.casino.blackjack.model;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a hand of cards for player or dealer.
 * Handles flexible Ace (1 or 11) scoring.
 */
public class Hand implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Card> cards = new ArrayList<>();
    private double bet;
    private boolean isDoubledDown=false, isSplit=false, isInsured=false;
    private double insuranceBet=0;
    private boolean stood=false, surrendered=false;

    public Hand(){this.bet=0;}
    public Hand(double bet){this.bet=bet;}

    public void addCard(Card card){cards.add(card);}
    public List<Card> getCards(){return Collections.unmodifiableList(cards);}
    public Card getCard(int i){return cards.get(i);}
    public int getCardCount(){return cards.size();}

    public void clear(){
        cards.clear();
        isDoubledDown=false;isSplit=false;isInsured=false;
        insuranceBet=0;stood=false;surrendered=false;
    }

    /** Best value ≤ 21, or lowest bust value. Only counts face-up cards. */
    public int getValue(){
        int total=0,aces=0;
        for(Card c:cards){
            if(!c.isFaceUp())continue;
            total+=c.getValue();
            if(c.isAce())aces++;
        }
        while(total>21&&aces>0){total-=10;aces--;}
        return total;
    }

    /** True value including face-down cards (for dealer logic). */
    public int getTrueValue(){
        int total=0,aces=0;
        for(Card c:cards){total+=c.getValue();if(c.isAce())aces++;}
        while(total>21&&aces>0){total-=10;aces--;}
        return total;
    }

    public boolean isBusted(){return getValue()>21;}
    public boolean isBlackjack(){return cards.size()==2&&getValue()==21&&!isSplit;}

    public boolean isSoft(){
        int total=0,aces=0;
        for(Card c:cards){
            if(!c.isFaceUp())continue;
            total+=c.getValue();if(c.isAce())aces++;
        }
        int saved=aces;
        while(total>21&&aces>0){total-=10;aces--;}
        int hard=0;
        for(Card c:cards){if(!c.isFaceUp())continue;hard+=c.isAce()?1:c.getValue();}
        return total!=hard;
    }

    public boolean canSplit(){
        if(cards.size()!=2)return false;
        return cards.get(0).getRank()==cards.get(1).getRank()
            ||(cards.get(0).getValue()==10&&cards.get(1).getValue()==10);
    }

    public boolean canDoubleDown(){return cards.size()==2&&!isDoubledDown;}

    public double getBet(){return bet;}
    public void setBet(double b){this.bet=b;}
    public void doDoubleDown(double extra){this.bet+=extra;this.isDoubledDown=true;}
    public void setInsurance(double ib){this.isInsured=true;this.insuranceBet=ib;}
    public double getInsuranceBet(){return insuranceBet;}
    public boolean isInsured(){return isInsured;}
    public boolean isDoubledDown(){return isDoubledDown;}
    public boolean isSplitHand(){return isSplit;}
    public void setIsSplit(boolean s){this.isSplit=s;}
    public boolean hasStood(){return stood;}
    public void stand(){this.stood=true;}
    public boolean isSurrendered(){return surrendered;}
    public void surrender(){this.surrendered=true;}

    @Override public String toString(){
        StringBuilder sb=new StringBuilder("[");
        for(int i=0;i<cards.size();i++){sb.append(cards.get(i));if(i<cards.size()-1)sb.append(", ");}
        return sb.append("] = ").append(getValue()).toString();
    }
}
