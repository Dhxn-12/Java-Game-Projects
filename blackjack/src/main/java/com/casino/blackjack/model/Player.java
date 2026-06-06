package com.casino.blackjack.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Human player profile, wallet, statistics, achievements.
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String avatarKey = "default";
    private LocalDate createdDate, lastPlayedDate;

    private double balance, totalWagered, totalWon, totalLost;
    private double highestBalance, biggestWin;

    private int handsPlayed, handsWon, handsLost, handsPushed;
    private int blackjacks, busts, doubleDownWins, splitWins, insuranceWins;
    private int streakCurrent, streakBest;
    private boolean lastHandWon = false;

    private LocalDate lastDailyReward;
    private int consecutiveDays;

    private final List<Hand> hands = new ArrayList<>();
    private int activeHandIndex = 0;
    private final List<String> achievements = new ArrayList<>();

    public Player(String name, double startingBalance){
        this.name=name; this.balance=startingBalance;
        this.highestBalance=startingBalance;
        this.createdDate=LocalDate.now(); this.lastPlayedDate=LocalDate.now();
    }

    // Hand management
    public void clearHands(){hands.clear();activeHandIndex=0;}
    public void addHand(Hand h){hands.add(h);}
    public Hand getActiveHand(){return hands.isEmpty()?null:hands.get(activeHandIndex);}
    public List<Hand> getHands(){return hands;}
    public int getActiveHandIndex(){return activeHandIndex;}
    public void nextHand(){if(activeHandIndex<hands.size()-1)activeHandIndex++;}
    public boolean hasMoreHands(){return activeHandIndex<hands.size()-1;}

    // Wallet
    public boolean canBet(double amount){return balance>=amount;}
    public void deductBet(double amount){balance-=amount;totalWagered+=amount;}
    public void addWinnings(double amount){
        balance+=amount;totalWon+=amount;
        if(balance>highestBalance)highestBalance=balance;
    }
    public void addLoss(double amount){totalLost+=amount;}

    // Stats
    public void recordWin(boolean bj){
        handsPlayed++;handsWon++;if(bj)blackjacks++;
        if(lastHandWon)streakCurrent++;else streakCurrent=1;
        lastHandWon=true;
        if(streakCurrent>streakBest)streakBest=streakCurrent;
    }
    public void recordLoss(){handsPlayed++;handsLost++;streakCurrent=0;lastHandWon=false;}
    public void recordPush(){handsPlayed++;handsPushed++;}
    public void recordBust(){busts++;}
    public void recordDoubleDownWin(){doubleDownWins++;}
    public void recordSplitWin(){splitWins++;}
    public void recordInsuranceWin(){insuranceWins++;}
    public double getWinRate(){return handsPlayed>0?(double)handsWon/handsPlayed*100:0;}
    public double getNetProfit(){return totalWon-totalLost;}

    // Daily reward
    public boolean canClaimDailyReward(){
        return lastDailyReward==null||!lastDailyReward.equals(LocalDate.now());
    }
    public double claimDailyReward(){
        LocalDate today=LocalDate.now();
        if(lastDailyReward!=null&&lastDailyReward.equals(today.minusDays(1)))consecutiveDays++;
        else consecutiveDays=1;
        lastDailyReward=today;
        double reward=100+(consecutiveDays*50);
        balance+=reward;
        return reward;
    }

    // Achievements
    public void unlockAchievement(String id){if(!achievements.contains(id))achievements.add(id);}
    public boolean hasAchievement(String id){return achievements.contains(id);}
    public List<String> getAchievements(){return achievements;}

    // Getters/Setters
    public String getName(){return name;}
    public void setName(String n){this.name=n;}
    public String getAvatarKey(){return avatarKey;}
    public void setAvatarKey(String k){this.avatarKey=k;}
    public double getBalance(){return balance;}
    public void setBalance(double b){this.balance=b;}
    public int getHandsPlayed(){return handsPlayed;}
    public int getHandsWon(){return handsWon;}
    public int getHandsLost(){return handsLost;}
    public int getHandsPushed(){return handsPushed;}
    public int getBlackjacks(){return blackjacks;}
    public int getBusts(){return busts;}
    public int getStreakBest(){return streakBest;}
    public int getStreakCurrent(){return streakCurrent;}
    public double getTotalWagered(){return totalWagered;}
    public double getTotalWon(){return totalWon;}
    public double getTotalLost(){return totalLost;}
    public double getHighestBalance(){return highestBalance;}
    public double getBiggestWin(){return biggestWin;}
    public int getConsecutiveDays(){return consecutiveDays;}
    public LocalDate getLastDailyReward(){return lastDailyReward;}

    @Override public String toString(){
        return String.format("Player[%s, $%.2f, W:%d L:%d BJ:%d]",name,balance,handsWon,handsLost,blackjacks);
    }
}
