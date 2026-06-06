package com.casino.blackjack.model;

import java.io.Serializable;

/**
 * All user-configurable game settings — persisted via SaveSystem.
 */
public class GameSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private int    deckCount          = 6;
    private double blackjackPayout    = 1.5;
    private boolean dealerPeek        = true;
    private boolean standOnSoft17     = true;
    private boolean doubleAfterSplit  = true;
    private boolean surrenderAllowed  = true;
    private double minimumBet         = 10;
    private double maximumBet         = 1000;
    private double startingBalance    = 1000;
    private boolean soundEnabled      = true;
    private boolean musicEnabled      = true;
    private float  soundVolume        = 0.8f;
    private float  musicVolume        = 0.4f;
    private String theme              = "CLASSIC_GREEN";
    private boolean fullscreen        = false;
    private boolean showHints         = true;
    private boolean showProbabilities = false;
    private boolean animationsEnabled = true;
    private int    animationSpeed     = 2;
    private boolean timerEnabled      = false;
    private int    timerSeconds       = 30;
    private String difficulty         = "NORMAL";

    public int    getDeckCount(){return deckCount;}
    public void   setDeckCount(int n){deckCount=Math.max(1,Math.min(8,n));}
    public double getBlackjackPayout(){return blackjackPayout;}
    public void   setBlackjackPayout(double p){blackjackPayout=p;}
    public boolean isDealerPeek(){return dealerPeek;}
    public void   setDealerPeek(boolean b){dealerPeek=b;}
    public boolean isStandOnSoft17(){return standOnSoft17;}
    public void   setStandOnSoft17(boolean b){standOnSoft17=b;}
    public boolean isDoubleAfterSplit(){return doubleAfterSplit;}
    public void   setDoubleAfterSplit(boolean b){doubleAfterSplit=b;}
    public boolean isSurrenderAllowed(){return surrenderAllowed;}
    public void   setSurrenderAllowed(boolean b){surrenderAllowed=b;}
    public double getMinimumBet(){return minimumBet;}
    public void   setMinimumBet(double m){minimumBet=m;}
    public double getMaximumBet(){return maximumBet;}
    public void   setMaximumBet(double m){maximumBet=m;}
    public double getStartingBalance(){return startingBalance;}
    public void   setStartingBalance(double b){startingBalance=b;}
    public boolean isSoundEnabled(){return soundEnabled;}
    public void   setSoundEnabled(boolean b){soundEnabled=b;}
    public boolean isMusicEnabled(){return musicEnabled;}
    public void   setMusicEnabled(boolean b){musicEnabled=b;}
    public float  getSoundVolume(){return soundVolume;}
    public void   setSoundVolume(float v){soundVolume=v;}
    public float  getMusicVolume(){return musicVolume;}
    public void   setMusicVolume(float v){musicVolume=v;}
    public String getTheme(){return theme;}
    public void   setTheme(String t){theme=t;}
    public boolean isFullscreen(){return fullscreen;}
    public void   setFullscreen(boolean b){fullscreen=b;}
    public boolean isShowHints(){return showHints;}
    public void   setShowHints(boolean b){showHints=b;}
    public boolean isShowProbabilities(){return showProbabilities;}
    public void   setShowProbabilities(boolean b){showProbabilities=b;}
    public boolean isAnimationsEnabled(){return animationsEnabled;}
    public void   setAnimationsEnabled(boolean b){animationsEnabled=b;}
    public int    getAnimationSpeed(){return animationSpeed;}
    public void   setAnimationSpeed(int s){animationSpeed=s;}
    public boolean isTimerEnabled(){return timerEnabled;}
    public void   setTimerEnabled(boolean b){timerEnabled=b;}
    public int    getTimerSeconds(){return timerSeconds;}
    public void   setTimerSeconds(int s){timerSeconds=s;}
    public String getDifficulty(){return difficulty;}
    public void   setDifficulty(String d){difficulty=d;}

    public int getDealDelayMs(){
        return switch(animationSpeed){case 1->600;case 3->200;default->350;};
    }
}
