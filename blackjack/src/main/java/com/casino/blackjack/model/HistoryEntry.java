package com.casino.blackjack.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Snapshot of one completed round for history/replay.
 */
public class HistoryEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDateTime timestamp;
    private final List<String> playerCards, dealerCards;
    private final int playerValue, dealerValue;
    private final double betAmount, netResult, balanceAfter;
    private final RoundResult result;
    private final boolean wasSplit, wasDoubleDown;

    public HistoryEntry(Player player, Dealer dealer, RoundResult result,
                        double netResult, boolean wasSplit, boolean wasDoubleDown){
        this.timestamp=LocalDateTime.now();
        this.result=result; this.netResult=netResult;
        this.wasSplit=wasSplit; this.wasDoubleDown=wasDoubleDown;
        this.balanceAfter=player.getBalance();
        Hand ph=player.getActiveHand();
        this.betAmount=ph!=null?ph.getBet():0;
        this.playerValue=ph!=null?ph.getValue():0;
        this.dealerValue=dealer.getHandValue();
        this.playerCards=new ArrayList<>();
        if(ph!=null)for(Card c:ph.getCards())playerCards.add(c.getShortDisplay());
        this.dealerCards=new ArrayList<>();
        for(Card c:dealer.getHand().getCards())dealerCards.add(c.getShortDisplay());
    }

    public LocalDateTime getTimestamp(){return timestamp;}
    public List<String> getPlayerCards(){return playerCards;}
    public List<String> getDealerCards(){return dealerCards;}
    public int getPlayerValue(){return playerValue;}
    public int getDealerValue(){return dealerValue;}
    public double getBetAmount(){return betAmount;}
    public double getNetResult(){return netResult;}
    public RoundResult getResult(){return result;}
    public boolean wasSplit(){return wasSplit;}
    public boolean wasDoubleDown(){return wasDoubleDown;}
    public double getBalanceAfter(){return balanceAfter;}

    @Override public String toString(){
        return String.format("[%s] %s | P:%d D:%d | Bet:$%.0f Net:%+.0f | Bal:$%.0f",
                timestamp.toLocalTime(),result.getLabel(),
                playerValue,dealerValue,betAmount,netResult,balanceAfter);
    }
}
