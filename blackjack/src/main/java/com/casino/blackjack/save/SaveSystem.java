package com.casino.blackjack.save;

import com.casino.blackjack.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

/**
 * Singleton – serialises player profiles, settings, history, leaderboard.
 */
public class SaveSystem {
    private static final Logger LOG = Logger.getLogger(SaveSystem.class.getName());

    private static SaveSystem instance;
    private SaveSystem(){}
    public static synchronized SaveSystem getInstance(){
        if(instance==null)instance=new SaveSystem();
        return instance;
    }

    private static final String SAVE_DIR         = System.getProperty("user.home")+"/.blackjack";
    private static final String PLAYER_FILE      = SAVE_DIR+"/player.dat";
    private static final String SETTINGS_FILE    = SAVE_DIR+"/settings.dat";
    private static final String HISTORY_FILE     = SAVE_DIR+"/history.dat";
    private static final String LEADERBOARD_FILE = SAVE_DIR+"/leaderboard.dat";

    public void init(){
        try{Files.createDirectories(Paths.get(SAVE_DIR));}
        catch(IOException e){LOG.warning("Save dir error: "+e.getMessage());}
    }

    public boolean savePlayer(Player p){return saveObject(p,PLAYER_FILE);}
    public Player loadPlayer(){return loadObject(PLAYER_FILE,Player.class);}
    public boolean playerSaveExists(){return Files.exists(Paths.get(PLAYER_FILE));}

    public boolean saveSettings(GameSettings s){return saveObject(s,SETTINGS_FILE);}
    public GameSettings loadSettings(){
        GameSettings s=loadObject(SETTINGS_FILE,GameSettings.class);
        return s!=null?s:new GameSettings();
    }

    @SuppressWarnings("unchecked")
    public List<HistoryEntry> loadHistory(){
        List<HistoryEntry> h=loadObject(HISTORY_FILE,ArrayList.class);
        return h!=null?h:new ArrayList<>();
    }
    public boolean saveHistory(List<HistoryEntry> history){
        List<HistoryEntry> t=history.size()>500?history.subList(history.size()-500,history.size()):history;
        return saveObject(new ArrayList<>(t),HISTORY_FILE);
    }

    @SuppressWarnings("unchecked")
    public List<LeaderboardEntry> loadLeaderboard(){
        List<LeaderboardEntry> lb=loadObject(LEADERBOARD_FILE,ArrayList.class);
        return lb!=null?lb:new ArrayList<>();
    }
    public boolean saveLeaderboardEntry(String name,double balance,int wins,double winRate){
        List<LeaderboardEntry> lb=loadLeaderboard();
        lb.add(new LeaderboardEntry(name,balance,wins,winRate));
        lb.sort(Comparator.comparingDouble(LeaderboardEntry::getBalance).reversed());
        if(lb.size()>100)lb=lb.subList(0,100);
        return saveObject(new ArrayList<>(lb),LEADERBOARD_FILE);
    }

    public String createBackup(){
        String ts=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String bd=SAVE_DIR+"/backups/"+ts;
        try{
            Files.createDirectories(Paths.get(bd));
            for(String src:List.of(PLAYER_FILE,SETTINGS_FILE,HISTORY_FILE)){
                Path sp=Paths.get(src);
                if(Files.exists(sp))Files.copy(sp,Paths.get(bd+"/"+sp.getFileName()));
            }
            return bd;
        }catch(IOException e){LOG.warning("Backup failed: "+e.getMessage());return null;}
    }

    public boolean deleteSave(){
        try{
            Files.deleteIfExists(Paths.get(PLAYER_FILE));
            Files.deleteIfExists(Paths.get(HISTORY_FILE));
            return true;
        }catch(IOException e){return false;}
    }

    private <T> boolean saveObject(T obj,String path){
        try(ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(path))){
            oos.writeObject(obj);return true;
        }catch(IOException e){LOG.warning("Save failed ["+path+"]: "+e.getMessage());return false;}
    }

    @SuppressWarnings("unchecked")
    private <T> T loadObject(String path,Class<T> type){
        if(!Files.exists(Paths.get(path)))return null;
        try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream(path))){
            return (T)ois.readObject();
        }catch(Exception e){LOG.warning("Load failed ["+path+"]: "+e.getMessage());return null;}
    }

    public static class LeaderboardEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name, timestamp;
        private final double balance, winRate;
        private final int wins;
        public LeaderboardEntry(String name,double balance,int wins,double winRate){
            this.name=name;this.balance=balance;this.wins=wins;this.winRate=winRate;
            this.timestamp=LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        public String getName(){return name;}
        public double getBalance(){return balance;}
        public int getWins(){return wins;}
        public double getWinRate(){return winRate;}
        public String getTimestamp(){return timestamp;}
    }
}
