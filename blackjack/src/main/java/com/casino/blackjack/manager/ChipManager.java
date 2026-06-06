package com.casino.blackjack.manager;

import java.util.List;

/**
 * Manages chip denominations and bet operations.
 */
public class ChipManager {

    public record Chip(double value, String label, String colorHex){}

    public static final List<Chip> CHIPS = List.of(
        new Chip(1,    "$1",   "#FFFFFF"),
        new Chip(5,    "$5",   "#CC0000"),
        new Chip(10,   "$10",  "#0055CC"),
        new Chip(25,   "$25",  "#228B22"),
        new Chip(50,   "$50",  "#FF6600"),
        new Chip(100,  "$100", "#1A1A2E"),
        new Chip(500,  "$500", "#6A0DAD"),
        new Chip(1000, "$1K",  "#CC9900")
    );

    private double currentBet=0, maxBet, minBet, playerBalance, lastBet=0;

    public ChipManager(double balance, double minBet, double maxBet){
        this.playerBalance=balance; this.minBet=minBet; this.maxBet=maxBet;
    }

    public boolean addChip(Chip chip){
        double nb=currentBet+chip.value();
        if(nb>maxBet||nb>playerBalance)return false;
        currentBet=nb; return true;
    }
    public boolean addAmount(double amount){
        double nb=currentBet+amount;
        if(nb>maxBet||nb>playerBalance)return false;
        currentBet=nb; return true;
    }
    public void clearBet(){if(currentBet>0)lastBet=currentBet;currentBet=0;}
    public boolean reBet(){
        if(lastBet<=0||lastBet>playerBalance||lastBet>maxBet)return false;
        currentBet=lastBet; return true;
    }
    public boolean doubleBet(){
        double d=currentBet*2;
        if(d>maxBet||d>playerBalance)return false;
        currentBet=d; return true;
    }
    public boolean maxBet(){
        double mb=Math.min(maxBet,playerBalance);
        if(mb<minBet)return false;
        currentBet=mb; return true;
    }
    public void removeLastChip(){
        double step=1;
        for(Chip c:CHIPS)if(currentBet>=c.value())step=c.value();
        currentBet=Math.max(0,currentBet-step);
    }
    public boolean isBetValid(){return currentBet>=minBet&&currentBet<=maxBet&&currentBet<=playerBalance;}
    public double getCurrentBet(){return currentBet;}
    public double getLastBet(){return lastBet;}
    public double getMinBet(){return minBet;}
    public double getMaxBet(){return maxBet;}
    public void setBalance(double b){playerBalance=b;}
    public void setMinBet(double m){minBet=m;}
    public void setMaxBet(double m){maxBet=m;}
}
