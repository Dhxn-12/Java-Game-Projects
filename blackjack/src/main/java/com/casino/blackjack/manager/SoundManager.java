package com.casino.blackjack.manager;

import javax.sound.sampled.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Singleton sound manager. Synthesises tones in-code (no external WAV files needed).
 */
public class SoundManager {
    private static final Logger LOG = Logger.getLogger(SoundManager.class.getName());

    private static SoundManager instance;
    private SoundManager(){}
    public static synchronized SoundManager getInstance(){
        if(instance==null)instance=new SoundManager();
        return instance;
    }

    public enum SoundEffect {
        CARD_DEAL, CARD_FLIP, CHIP_BET, CHIP_COLLECT,
        WIN, LOSE, BLACKJACK, PUSH, BUST,
        BUTTON_CLICK, SHUFFLE, DEAL_START,
        ACHIEVEMENT, DAILY_REWARD
    }

    private boolean soundEnabled=true, musicEnabled=true;
    private float soundVolume=0.8f, musicVolume=0.4f;
    private Clip musicClip;
    private final ExecutorService executor=Executors.newCachedThreadPool(r->{
        Thread t=new Thread(r,"SoundThread");t.setDaemon(true);return t;
    });

    private static final Map<SoundEffect,float[]> TONES=new EnumMap<>(SoundEffect.class);
    static{
        TONES.put(SoundEffect.CARD_DEAL,    new float[]{800,60});
        TONES.put(SoundEffect.CARD_FLIP,    new float[]{600,80});
        TONES.put(SoundEffect.CHIP_BET,     new float[]{1200,40});
        TONES.put(SoundEffect.CHIP_COLLECT, new float[]{1000,120});
        TONES.put(SoundEffect.WIN,          new float[]{880,200});
        TONES.put(SoundEffect.LOSE,         new float[]{220,300});
        TONES.put(SoundEffect.BLACKJACK,    new float[]{1047,400});
        TONES.put(SoundEffect.PUSH,         new float[]{500,150});
        TONES.put(SoundEffect.BUST,         new float[]{180,250});
        TONES.put(SoundEffect.BUTTON_CLICK, new float[]{700,30});
        TONES.put(SoundEffect.SHUFFLE,      new float[]{300,500});
        TONES.put(SoundEffect.DEAL_START,   new float[]{440,100});
        TONES.put(SoundEffect.ACHIEVEMENT,  new float[]{1320,300});
        TONES.put(SoundEffect.DAILY_REWARD, new float[]{987,250});
    }

    public void play(SoundEffect effect){
        if(!soundEnabled)return;
        executor.submit(()->playTone(effect));
    }

    public void playMusic(){if(musicEnabled)executor.submit(this::startAmbientMusic);}
    public void stopMusic(){if(musicClip!=null&&musicClip.isRunning())musicClip.stop();}
    public void setSoundEnabled(boolean b){soundEnabled=b;if(!b)stopMusic();}
    public void setMusicEnabled(boolean b){musicEnabled=b;if(b)playMusic();else stopMusic();}
    public void setSoundVolume(float v){soundVolume=Math.max(0,Math.min(1,v));}
    public void setMusicVolume(float v){musicVolume=Math.max(0,Math.min(1,v));}
    public boolean isSoundEnabled(){return soundEnabled;}
    public boolean isMusicEnabled(){return musicEnabled;}
    public float getSoundVolume(){return soundVolume;}
    public float getMusicVolume(){return musicVolume;}
    public void shutdown(){stopMusic();executor.shutdownNow();}

    private void playTone(SoundEffect effect){
        float[] params=TONES.getOrDefault(effect,new float[]{440,100});
        float freq=params[0];
        int dur=(int)params[1];
        try{
            AudioFormat fmt=new AudioFormat(44100,16,1,true,false);
            int samples=(int)(44100*dur/1000.0);
            byte[] buf=new byte[samples*2];
            for(int i=0;i<samples;i++){
                double angle=2.0*Math.PI*i*freq/44100;
                double pct=(double)i/samples;
                double env;
                if(pct<0.10)env=pct/0.10;
                else if(pct<0.80)env=1.0;
                else env=(1.0-pct)/0.20;
                short sample=(short)(Math.sin(angle)*env*32767*soundVolume);
                buf[i*2]=(byte)(sample&0xFF);buf[i*2+1]=(byte)((sample>>8)&0xFF);
            }
            DataLine.Info info=new DataLine.Info(SourceDataLine.class,fmt);
            SourceDataLine line=(SourceDataLine)AudioSystem.getLine(info);
            line.open(fmt);line.start();line.write(buf,0,buf.length);line.drain();line.close();
        }catch(Exception e){LOG.fine("Sound error: "+e.getMessage());}
    }

    private void startAmbientMusic(){
        try{
            AudioFormat fmt=new AudioFormat(44100,16,1,true,false);
            int ls=44100*4;byte[] buf=new byte[ls*2];
            float[] freqs={110f,138.6f,165f,220f};
            for(int i=0;i<ls;i++){
                double val=0;
                for(float f:freqs)val+=Math.sin(2.0*Math.PI*i*f/44100)/freqs.length;
                double tremolo=0.8+0.2*Math.sin(2.0*Math.PI*i*0.5/44100);
                short sample=(short)(val*tremolo*32767*musicVolume*0.3);
                buf[i*2]=(byte)(sample&0xFF);buf[i*2+1]=(byte)((sample>>8)&0xFF);
            }
            DataLine.Info info=new DataLine.Info(Clip.class,fmt);
            musicClip=(Clip)AudioSystem.getLine(info);
            musicClip.open(fmt,buf,0,buf.length);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);musicClip.start();
        }catch(Exception e){LOG.fine("Music error: "+e.getMessage());}
    }
}
