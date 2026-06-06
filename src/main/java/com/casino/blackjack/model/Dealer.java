package com.casino.blackjack.model;

import java.io.Serializable;

/**
 * Represents the dealer. Follows standard casino rules (S17 default).
 */
public class Dealer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Hand hand = new Hand();
    private boolean standOnSoft17 = true;

    public Hand getHand(){return hand;}
    public void clearHand(){hand.clear();}
    public void addCard(Card card){hand.addCard(card);}

    public void revealHoleCard(){for(Card c:hand.getCards())c.setFaceUp(true);}

    public Card getUpCard(){
        for(Card c:hand.getCards())if(c.isFaceUp())return c;
        return null;
    }
    public Card getHoleCard(){
        for(Card c:hand.getCards())if(!c.isFaceUp())return c;
        return null;
    }

    public boolean shouldHit(){
        int v=hand.getTrueValue();
        if(v<17)return true;
        if(v==17&&hand.isSoft()&&!standOnSoft17)return true;
        return false;
    }

    public boolean hasBlackjack(){return hand.isBlackjack();}
    public boolean upCardIsAce(){Card u=getUpCard();return u!=null&&u.isAce();}
    public boolean upCardIsTen(){Card u=getUpCard();return u!=null&&!u.isAce()&&u.getValue()==10;}
    public int getHandValue(){return hand.getTrueValue();}

    public void setStandOnSoft17(boolean s17){this.standOnSoft17=s17;}
    public boolean isStandOnSoft17(){return standOnSoft17;}

    @Override public String toString(){return "Dealer"+hand;}
}
