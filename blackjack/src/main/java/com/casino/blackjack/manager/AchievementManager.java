package com.casino.blackjack.manager;

import com.casino.blackjack.model.Player;
import java.util.*;

/**
 * Singleton – manages all achievement definitions and unlocking logic.
 */
public class AchievementManager {

    public record Achievement(String id, String title, String description, String icon, int pointValue){}

    public static final List<Achievement> ALL = List.of(
        new Achievement("FIRST_WIN",       "First Blood",        "Win your first hand",                  "🏆",10),
        new Achievement("BLACKJACK_1",     "Natural!",           "Get your first Blackjack",             "🃏",25),
        new Achievement("BLACKJACK_5",     "Blackjack Pro",      "Get 5 Blackjacks",                     "⭐",50),
        new Achievement("BLACKJACK_25",    "Blackjack Master",   "Get 25 Blackjacks",                    "👑",100),
        new Achievement("WIN_STREAK_3",    "Hot Streak",         "Win 3 hands in a row",                 "🔥",20),
        new Achievement("WIN_STREAK_5",    "On Fire!",           "Win 5 hands in a row",                 "🔥",50),
        new Achievement("WIN_STREAK_10",   "Unstoppable",        "Win 10 hands in a row",                "💎",150),
        new Achievement("BALANCE_2X",      "Doubling Up",        "Double your starting balance",         "💰",30),
        new Achievement("BALANCE_5X",      "High Roller",        "Reach 5x starting balance",            "💸",75),
        new Achievement("BALANCE_10X",     "Casino King",        "Reach 10x starting balance",           "🤑",200),
        new Achievement("HANDS_50",        "Regular",            "Play 50 hands",                        "🎴",20),
        new Achievement("HANDS_200",       "Veteran",            "Play 200 hands",                       "🎖️",50),
        new Achievement("HANDS_500",       "Grinder",            "Play 500 hands",                       "⚙️",100),
        new Achievement("DOUBLE_DOWN_WIN", "Double Trouble",     "Win a Double Down hand",               "✌️",25),
        new Achievement("SPLIT_WIN",       "Divide and Conquer", "Win after splitting",                  "✂️",25),
        new Achievement("INSURANCE_WIN",   "Covered!",           "Win an insurance bet",                 "🛡️",30),
        new Achievement("DAILY_7",         "Week Warrior",       "Log in 7 days in a row",               "📅",40),
        new Achievement("DAILY_30",        "Monthly Devotee",    "Log in 30 days in a row",              "📆",100),
        new Achievement("BIG_WIN",         "Jackpot!",           "Win $500+ in a single hand",           "🎰",60),
        new Achievement("MAX_BET_WIN",     "All In",             "Win a max-bet hand",                   "🎲",75),
        new Achievement("WIN_RATE_60",     "Skilled Player",     "Reach 60% win rate (50+ hands)",       "📊",80)
    );

    private static AchievementManager instance;
    private AchievementManager(){}
    public static synchronized AchievementManager getInstance(){
        if(instance==null)instance=new AchievementManager();
        return instance;
    }

    private final List<Achievement> recentUnlocks=new ArrayList<>();

    public List<Achievement> checkAndUnlock(Player p){
        recentUnlocks.clear();
        check(p,"FIRST_WIN",     p.getHandsWon()>=1);
        check(p,"BLACKJACK_1",   p.getBlackjacks()>=1);
        check(p,"BLACKJACK_5",   p.getBlackjacks()>=5);
        check(p,"BLACKJACK_25",  p.getBlackjacks()>=25);
        check(p,"WIN_STREAK_3",  p.getStreakCurrent()>=3);
        check(p,"WIN_STREAK_5",  p.getStreakCurrent()>=5);
        check(p,"WIN_STREAK_10", p.getStreakCurrent()>=10);
        check(p,"HANDS_50",      p.getHandsPlayed()>=50);
        check(p,"HANDS_200",     p.getHandsPlayed()>=200);
        check(p,"HANDS_500",     p.getHandsPlayed()>=500);
        check(p,"DAILY_7",       p.getConsecutiveDays()>=7);
        check(p,"DAILY_30",      p.getConsecutiveDays()>=30);
        check(p,"WIN_RATE_60",   p.getHandsPlayed()>=50&&p.getWinRate()>=60);
        return new ArrayList<>(recentUnlocks);
    }

    public void checkBalanceAchievements(Player p, double startBal){
        check(p,"BALANCE_2X",  p.getBalance()>=startBal*2);
        check(p,"BALANCE_5X",  p.getBalance()>=startBal*5);
        check(p,"BALANCE_10X", p.getBalance()>=startBal*10);
    }
    public void checkDoubleDownWin(Player p){check(p,"DOUBLE_DOWN_WIN",true);}
    public void checkSplitWin(Player p){check(p,"SPLIT_WIN",true);}
    public void checkInsuranceWin(Player p){check(p,"INSURANCE_WIN",true);}
    public void checkBigWin(Player p,double win){check(p,"BIG_WIN",win>=500);}
    public void checkMaxBetWin(Player p,double bet,double max){check(p,"MAX_BET_WIN",bet>=max);}

    private void check(Player p,String id,boolean cond){
        if(cond&&!p.hasAchievement(id)){
            p.unlockAchievement(id);
            Achievement a=findById(id);
            if(a!=null)recentUnlocks.add(a);
        }
    }

    public static Achievement findById(String id){
        return ALL.stream().filter(a->a.id().equals(id)).findFirst().orElse(null);
    }

    public int getTotalPoints(Player p){
        return ALL.stream().filter(a->p.hasAchievement(a.id())).mapToInt(Achievement::pointValue).sum();
    }
}
