package com.casino.blackjack.model;

/**
 * Outcome for a single hand at the end of a round.
 */
public enum RoundResult {
    WIN("WIN","+",0xFFD700),
    BLACKJACK("BLACKJACK!","BJ",0xFFD700),
    LOSE("LOSE","-",0xFF4444),
    PUSH("PUSH","=",0xAAAAAA),
    BUST("BUST","✗",0xFF4444),
    SURRENDER("SURRENDER","½",0xAAAAAA),
    INSURANCE_WIN("INSURANCE","INS",0x44FF88);

    private final String label, shortLabel;
    private final int color;
    RoundResult(String l,String s,int c){label=l;shortLabel=s;color=c;}
    public String getLabel(){return label;}
    public String getShortLabel(){return shortLabel;}
    public int getColor(){return color;}
    public boolean isWin(){return this==WIN||this==BLACKJACK||this==INSURANCE_WIN;}
    public boolean isLoss(){return this==LOSE||this==BUST;}
    public boolean isPush(){return this==PUSH||this==SURRENDER;}
}
