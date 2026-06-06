package com.casino.blackjack.controller;

import com.casino.blackjack.ai.BasicStrategy;
import com.casino.blackjack.manager.*;
import com.casino.blackjack.model.*;
import com.casino.blackjack.save.SaveSystem;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Singleton GameManager – central MVC controller.
 * Owns all game state, fires events to observers, exposes player actions.
 *
 * Patterns: Singleton, Observer, MVC, Factory (Deck via settings).
 */
public class GameManager {
    private static final Logger LOG = Logger.getLogger(GameManager.class.getName());

    private static GameManager instance;
    private GameManager(){}
    public static synchronized GameManager getInstance(){
        if(instance==null)instance=new GameManager();
        return instance;
    }

    // ── Core state ────────────────────────────────────────────────────────────
    private GameSettings settings = new GameSettings();
    private Player player;
    private final Dealer dealer   = new Dealer();
    private Deck deck;
    private ChipManager chipManager;
    private GameState state       = GameState.MAIN_MENU;
    private final List<HistoryEntry> history = new ArrayList<>();
    private String currentHint   = "";

    // ── Observers ─────────────────────────────────────────────────────────────
    private final List<GameObserver> observers = new CopyOnWriteArrayList<>();

    // ── Threading ─────────────────────────────────────────────────────────────
    private final ScheduledExecutorService scheduler =
        Executors.newSingleThreadScheduledExecutor(r->{
            Thread t=new Thread(r,"GameLoop");t.setDaemon(true);return t;
        });

    private ScheduledFuture<?> timerFuture;
    private int timerRemaining;

    // ═════════════════════════════════════════════════════════════════════════
    // INIT
    // ═════════════════════════════════════════════════════════════════════════
    public void init(){
        SaveSystem.getInstance().init();
        settings=SaveSystem.getInstance().loadSettings();
        history.addAll(SaveSystem.getInstance().loadHistory());
        if(SaveSystem.getInstance().playerSaveExists())
            player=SaveSystem.getInstance().loadPlayer();
        SoundManager sm=SoundManager.getInstance();
        sm.setSoundEnabled(settings.isSoundEnabled());
        sm.setMusicEnabled(settings.isMusicEnabled());
        sm.setSoundVolume(settings.getSoundVolume());
        sm.setMusicVolume(settings.getMusicVolume());
        if(settings.isMusicEnabled())sm.playMusic();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // OBSERVER
    // ═════════════════════════════════════════════════════════════════════════
    public void addObserver(GameObserver o){observers.add(o);}
    public void removeObserver(GameObserver o){observers.remove(o);}
    private void fire(GameEvent e,Object p){
        for(GameObserver o:observers){try{o.onGameEvent(e,p);}catch(Exception ex){LOG.warning(ex.getMessage());}}
    }
    private void fire(GameEvent e){fire(e,null);}

    // ═════════════════════════════════════════════════════════════════════════
    // STATE
    // ═════════════════════════════════════════════════════════════════════════
    private void setState(GameState s){state=s;fire(GameEvent.STATE_CHANGED,s);}
    public GameState getState(){return state;}

    // ═════════════════════════════════════════════════════════════════════════
    // PROFILE
    // ═════════════════════════════════════════════════════════════════════════
    public void createNewPlayer(String name){
        player=new Player(name,settings.getStartingBalance());
        initSession();
    }
    public void loadExistingPlayer(){
        if(player==null)return;
        initSession();
    }
    private void initSession(){
        chipManager=new ChipManager(player.getBalance(),settings.getMinimumBet(),settings.getMaximumBet());
        deck=new Deck(settings.getDeckCount());
        dealer.setStandOnSoft17(settings.isStandOnSoft17());
        if(player.canClaimDailyReward())fire(GameEvent.DAILY_REWARD_AVAILABLE);
        setState(GameState.PLACING_BET);
        fire(GameEvent.BALANCE_CHANGED,player.getBalance());
    }

    public double claimDailyReward(){
        if(player==null)return 0;
        double reward=player.claimDailyReward();
        chipManager.setBalance(player.getBalance());
        fire(GameEvent.DAILY_REWARD_CLAIMED,reward);
        fire(GameEvent.BALANCE_CHANGED,player.getBalance());
        autosave();
        return reward;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // BETTING
    // ═════════════════════════════════════════════════════════════════════════
    public boolean addChip(ChipManager.Chip chip){
        boolean ok=chipManager.addChip(chip);
        if(ok){SoundManager.getInstance().play(SoundManager.SoundEffect.CHIP_BET);fire(GameEvent.BET_PLACED,chipManager.getCurrentBet());}
        return ok;
    }
    public void clearBet(){chipManager.clearBet();fire(GameEvent.BET_CLEARED);}
    public boolean reBet(){boolean ok=chipManager.reBet();if(ok)fire(GameEvent.BET_PLACED,chipManager.getCurrentBet());return ok;}
    public boolean doubleBet(){boolean ok=chipManager.doubleBet();if(ok)fire(GameEvent.BET_PLACED,chipManager.getCurrentBet());return ok;}
    public boolean maxBet(){boolean ok=chipManager.maxBet();if(ok)fire(GameEvent.BET_PLACED,chipManager.getCurrentBet());return ok;}
    public double getCurrentBet(){return chipManager.getCurrentBet();}
    public boolean isBetValid(){return chipManager.isBetValid();}

    // ═════════════════════════════════════════════════════════════════════════
    // ROUND FLOW
    // ═════════════════════════════════════════════════════════════════════════
    public void startRound(){
        if(!isBetValid()||state!=GameState.PLACING_BET)return;
        SoundManager.getInstance().play(SoundManager.SoundEffect.DEAL_START);
        if(deck==null||deck.needsReshuffle()){
            deck=new Deck(settings.getDeckCount());
            fire(GameEvent.SHOE_RESHUFFLED);
            SoundManager.getInstance().play(SoundManager.SoundEffect.SHUFFLE);
        }
        double bet=chipManager.getCurrentBet();
        player.deductBet(bet);
        fire(GameEvent.BALANCE_CHANGED,player.getBalance());
        player.clearHands();
        Hand hand=new Hand(bet);
        player.addHand(hand);
        dealer.clearHand();
        setState(GameState.DEALING);
        fire(GameEvent.ROUND_STARTED);
        scheduleDeal(hand);
    }

    private void scheduleDeal(Hand hand){
        int d=settings.getDealDelayMs();
        scheduler.schedule(()->dealCard(hand,true,1),     (long)d,   TimeUnit.MILLISECONDS);
        scheduler.schedule(()->dealCard(dealer.getHand(),true,2), (long)d*2, TimeUnit.MILLISECONDS);
        scheduler.schedule(()->dealCard(hand,true,3),     (long)d*3, TimeUnit.MILLISECONDS);
        scheduler.schedule(()->{dealCard(dealer.getHand(),false,4);afterDeal(hand);},(long)d*4,TimeUnit.MILLISECONDS);
    }

    private void dealCard(Hand hand,boolean faceUp,int seq){
        Card card=faceUp?deck.draw():deck.drawFaceDown();
        hand.addCard(card);
        SoundManager.getInstance().play(SoundManager.SoundEffect.CARD_DEAL);
        fire(GameEvent.CARD_DEALT,new Object[]{card,hand,seq});
    }

    private void afterDeal(Hand ph){
        if(settings.isDealerPeek()){
            if(dealer.upCardIsAce()){setState(GameState.INSURANCE_OFFER);fire(GameEvent.INSURANCE_OFFERED);return;}
            if(dealer.upCardIsTen()&&dealer.hasBlackjack()){resolveBlackjack(ph);return;}
        }
        if(ph.isBlackjack())resolveBlackjack(ph);
        else startPlayerTurn();
    }

    private void resolveBlackjack(Hand ph){
        dealer.revealHoleCard();fire(GameEvent.CARD_FLIPPED,dealer.getHand().getCards());
        if(ph.isBlackjack()&&dealer.hasBlackjack())settleHand(ph,RoundResult.PUSH);
        else if(ph.isBlackjack()){SoundManager.getInstance().play(SoundManager.SoundEffect.BLACKJACK);fire(GameEvent.BLACKJACK_DETECTED);settleHand(ph,RoundResult.BLACKJACK);}
        else settleHand(ph,RoundResult.LOSE);
        endRound();
    }

    // ── Insurance ────────────────────────────────────────────────────────────
    public void acceptInsurance(){
        double ia=player.getActiveHand().getBet()/2;
        if(!player.canBet(ia)){declineInsurance();return;}
        player.deductBet(ia);player.getActiveHand().setInsurance(ia);
        fire(GameEvent.BALANCE_CHANGED,player.getBalance());
        resolveInsurance();
    }
    public void declineInsurance(){resolveInsurance();}
    private void resolveInsurance(){
        Hand ph=player.getActiveHand();
        if(dealer.hasBlackjack()){
            if(ph.isInsured()){
                player.addWinnings(ph.getInsuranceBet()*3);
                player.recordInsuranceWin();
                AchievementManager.getInstance().checkInsuranceWin(player);
                fire(GameEvent.INSURANCE_RESOLVED,true);
                fire(GameEvent.BALANCE_CHANGED,player.getBalance());
            }
            dealer.revealHoleCard();fire(GameEvent.CARD_FLIPPED,dealer.getHand().getCards());
            settleHand(ph,RoundResult.LOSE);endRound();
        }else{
            fire(GameEvent.INSURANCE_RESOLVED,false);
            if(ph.isBlackjack())resolveBlackjack(ph);
            else startPlayerTurn();
        }
    }

    // ── Player actions ───────────────────────────────────────────────────────
    private void startPlayerTurn(){
        setState(GameState.PLAYER_TURN);
        updateHint();
        if(settings.isTimerEnabled())startTimer();
    }

    public void hit(){
        if(state!=GameState.PLAYER_TURN&&state!=GameState.PLAYER_TURN_SPLIT)return;
        Hand hand=player.getActiveHand();
        dealCard(hand,true,-1);
        if(hand.isBusted()){
            SoundManager.getInstance().play(SoundManager.SoundEffect.BUST);
            fire(GameEvent.PLAYER_BUST,hand);player.recordBust();
            advanceOrDealerTurn();
        }else if(hand.getValue()==21){stand();}
        else{updateHint();}
    }

    public void stand(){
        if(state!=GameState.PLAYER_TURN&&state!=GameState.PLAYER_TURN_SPLIT)return;
        stopTimer();player.getActiveHand().stand();advanceOrDealerTurn();
    }

    public void doubleDown(){
        if(state!=GameState.PLAYER_TURN)return;
        Hand hand=player.getActiveHand();
        if(!hand.canDoubleDown())return;
        double extra=hand.getBet();
        if(!player.canBet(extra))return;
        player.deductBet(extra);hand.doDoubleDown(extra);
        fire(GameEvent.BALANCE_CHANGED,player.getBalance());
        fire(GameEvent.DOUBLE_DOWN_PERFORMED,hand);
        dealCard(hand,true,-1);stand();
    }

    public void split(){
        if(state!=GameState.PLAYER_TURN)return;
        Hand hand=player.getActiveHand();
        if(!hand.canSplit()||!player.canBet(hand.getBet()))return;
        player.deductBet(hand.getBet());
        fire(GameEvent.BALANCE_CHANGED,player.getBalance());
        Hand hand2=new Hand(hand.getBet());
        Card c1=hand.getCards().get(0);Card c2=hand.getCards().get(1);
        hand.clear();hand.setBet(chipManager.getCurrentBet());
        hand.addCard(c1);hand.setIsSplit(true);
        hand2.addCard(c2);hand2.setIsSplit(true);
        player.addHand(hand2);
        dealCard(hand,true,-1);dealCard(hand2,true,-1);
        fire(GameEvent.SPLIT_PERFORMED,new Hand[]{hand,hand2});
        setState(GameState.PLAYER_TURN_SPLIT);updateHint();
    }

    public void surrender(){
        if(state!=GameState.PLAYER_TURN||!settings.isSurrenderAllowed())return;
        Hand hand=player.getActiveHand();
        if(hand.getCardCount()!=2)return;
        hand.surrender();fire(GameEvent.SURRENDER_PERFORMED,hand);
        player.addWinnings(hand.getBet()/2);
        fire(GameEvent.BALANCE_CHANGED,player.getBalance());
        settleHand(hand,RoundResult.SURRENDER);endRound();
    }

    private void advanceOrDealerTurn(){
        if(player.hasMoreHands()){player.nextHand();setState(GameState.PLAYER_TURN_SPLIT);updateHint();}
        else runDealerTurn();
    }

    private void runDealerTurn(){
        boolean allBusted=player.getHands().stream().allMatch(Hand::isBusted);
        dealer.revealHoleCard();fire(GameEvent.CARD_FLIPPED,dealer.getHand().getCards());
        if(allBusted){settleAllHands();endRound();return;}
        setState(GameState.DEALER_TURN);runDealerDraw();
    }

    private void runDealerDraw(){
        int d=settings.getDealDelayMs();
        scheduler.schedule(()->{
            if(dealer.shouldHit()){
                dealCard(dealer.getHand(),true,-1);
                if(dealer.getHand().isBusted()){fire(GameEvent.DEALER_BUST,dealer.getHand());settleAllHands();endRound();}
                else runDealerDraw();
            }else{settleAllHands();endRound();}
        },d,TimeUnit.MILLISECONDS);
    }

    // ── Settlement ───────────────────────────────────────────────────────────
    private void settleAllHands(){for(Hand h:player.getHands())if(!h.isSurrendered())settleHand(h,determineResult(h));}

    private RoundResult determineResult(Hand ph){
        if(ph.isBusted())return RoundResult.BUST;
        if(ph.isBlackjack())return RoundResult.BLACKJACK;
        int pv=ph.getValue(),dv=dealer.getHand().getTrueValue();
        if(dealer.getHand().isBusted())return RoundResult.WIN;
        if(pv>dv)return RoundResult.WIN;
        if(pv<dv)return RoundResult.LOSE;
        return RoundResult.PUSH;
    }

    private void settleHand(Hand hand,RoundResult result){
        double bet=hand.getBet(),payout=0;
        switch(result){
            case WIN->payout=bet*2;
            case BLACKJACK->payout=bet+bet*settings.getBlackjackPayout();
            case PUSH->payout=bet;
            case SURRENDER->payout=bet/2;
            default->payout=0;
        }
        if(payout>0)player.addWinnings(payout);
        else player.addLoss(bet);
        switch(result){
            case WIN,BLACKJACK->player.recordWin(result==RoundResult.BLACKJACK);
            case LOSE,BUST->player.recordLoss();
            case PUSH->player.recordPush();
            default->{}
        }
        double net=payout-bet;
        fire(GameEvent.HAND_RESULT,new Object[]{hand,result,net});
        fire(GameEvent.BALANCE_CHANGED,player.getBalance());
        if(result.isWin()){
            SoundManager.getInstance().play(result==RoundResult.BLACKJACK?SoundManager.SoundEffect.BLACKJACK:SoundManager.SoundEffect.WIN);
            SoundManager.getInstance().play(SoundManager.SoundEffect.CHIP_COLLECT);
        }else if(result.isLoss()){
            SoundManager.getInstance().play(SoundManager.SoundEffect.LOSE);
        }
        if(hand.isDoubledDown()&&result.isWin())AchievementManager.getInstance().checkDoubleDownWin(player);
        if(hand.isSplitHand()&&result.isWin())AchievementManager.getInstance().checkSplitWin(player);
        AchievementManager.getInstance().checkBigWin(player,payout-bet);
        AchievementManager.getInstance().checkMaxBetWin(player,bet,settings.getMaximumBet());
        AchievementManager.getInstance().checkBalanceAchievements(player,settings.getStartingBalance());
        List<AchievementManager.Achievement> unlocked=AchievementManager.getInstance().checkAndUnlock(player);
        for(AchievementManager.Achievement a:unlocked)fire(GameEvent.ACHIEVEMENT_UNLOCKED,a);
        history.add(new HistoryEntry(player,dealer,result,net,hand.isSplitHand(),hand.isDoubledDown()));
    }

    private void endRound(){
        stopTimer();
        chipManager.setBalance(player.getBalance());
        chipManager.clearBet();
        autosave();
        fire(GameEvent.ROUND_ENDED);
        if(player.getBalance()<settings.getMinimumBet())setState(GameState.GAME_OVER);
        else setState(GameState.PLACING_BET);
    }

    // ── Hint ──────────────────────────────────────────────────────────────────
    private void updateHint(){
        if(!settings.isShowHints())return;
        Hand hand=player.getActiveHand();Card upCard=dealer.getUpCard();
        if(hand!=null&&upCard!=null){currentHint=BasicStrategy.explain(hand,upCard);fire(GameEvent.HINT_UPDATED,currentHint);}
    }
    public String getCurrentHint(){return currentHint;}

    // ── Timer ──────────────────────────────────────────────────────────────────
    private void startTimer(){
        stopTimer();timerRemaining=settings.getTimerSeconds();
        timerFuture=scheduler.scheduleAtFixedRate(()->{
            timerRemaining--;fire(GameEvent.TIMER_TICK,timerRemaining);
            if(timerRemaining<=0){stopTimer();fire(GameEvent.TIMER_EXPIRED);stand();}
        },1,1,TimeUnit.SECONDS);
    }
    private void stopTimer(){if(timerFuture!=null&&!timerFuture.isCancelled())timerFuture.cancel(false);}

    // ── Navigation ───────────────────────────────────────────────────────────
    public void goToMainMenu(){setState(GameState.MAIN_MENU);}
    public void goToSettings(){setState(GameState.SETTINGS);}
    public void goToLeaderboard(){setState(GameState.LEADERBOARD);}
    public void goToStatistics(){setState(GameState.STATISTICS);}
    public void goToTutorial(){setState(GameState.TUTORIAL);}
    public void returnToGame(){setState(GameState.PLACING_BET);}

    // ── Settings ──────────────────────────────────────────────────────────────
    public void applySettings(GameSettings ns){
        this.settings=ns;
        SoundManager sm=SoundManager.getInstance();
        sm.setSoundEnabled(ns.isSoundEnabled());sm.setMusicEnabled(ns.isMusicEnabled());
        sm.setSoundVolume(ns.getSoundVolume());sm.setMusicVolume(ns.getMusicVolume());
        dealer.setStandOnSoft17(ns.isStandOnSoft17());
        if(chipManager!=null){chipManager.setMinBet(ns.getMinimumBet());chipManager.setMaxBet(ns.getMaximumBet());}
        SaveSystem.getInstance().saveSettings(ns);
        fire(GameEvent.SETTINGS_CHANGED,ns);
    }

    // ── Save ──────────────────────────────────────────────────────────────────
    private void autosave(){
        if(player!=null)SaveSystem.getInstance().savePlayer(player);
        SaveSystem.getInstance().saveHistory(history);
    }
    public boolean saveGame(){autosave();fire(GameEvent.SAVE_SUCCESS);return true;}

    // ── Getters ───────────────────────────────────────────────────────────────
    public Player getPlayer(){return player;}
    public Dealer getDealer(){return dealer;}
    public Deck getDeck(){return deck;}
    public GameSettings getSettings(){return settings;}
    public ChipManager getChipManager(){return chipManager;}
    public List<HistoryEntry> getHistory(){return history;}
    public int getTimerRemaining(){return timerRemaining;}

    public void shutdown(){autosave();stopTimer();scheduler.shutdownNow();SoundManager.getInstance().shutdown();}
}
