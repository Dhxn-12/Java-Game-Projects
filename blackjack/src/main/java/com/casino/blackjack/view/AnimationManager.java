package com.casino.blackjack.view;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Manages all card and UI animations using a single 60fps Swing Timer.
 */
public class AnimationManager {
    private static AnimationManager instance;
    private AnimationManager(){startLoop();}
    public static synchronized AnimationManager getInstance(){
        if(instance==null)instance=new AnimationManager();
        return instance;
    }

    public static class CardAnimation {
        public float x,y,targetX,targetY,alpha,rotation,scale,flipProgress;
        public boolean flipping,done;
        public Runnable onComplete;
        private static final float EASE=0.15f;

        CardAnimation(float sx,float sy,float tx,float ty){
            x=sx;y=sy;targetX=tx;targetY=ty;alpha=0f;scale=0.5f;
        }
        void update(){
            x+=(targetX-x)*EASE; y+=(targetY-y)*EASE;
            alpha=Math.min(1f,alpha+0.08f); scale=Math.min(1f,scale+0.05f);
            if(Math.abs(targetX-x)<1&&Math.abs(targetY-y)<1){
                x=targetX;y=targetY;alpha=1f;scale=1f;
                if(onComplete!=null){onComplete.run();onComplete=null;}
                if(!flipping)done=true;
            }
            if(flipping){flipProgress=Math.min(1f,flipProgress+0.06f);if(flipProgress>=1f){flipping=false;done=true;}}
        }
    }

    public static class TextAnimation {
        public String text; public float x,y,alpha=1f,vy=-1.5f; public Color color; public Font font; public boolean done;
        TextAnimation(String t,float x,float y,Color c,Font f){text=t;this.x=x;this.y=y;color=c;font=f;}
        void update(){y+=vy;vy*=0.97f;alpha=Math.max(0,alpha-0.012f);if(alpha<=0)done=true;}
    }

    public static class PulseAnimation {
        public float scale=1f,maxScale; public boolean expanding=true,done; public int cycles;
        PulseAnimation(float m,int c){maxScale=m;cycles=c;}
        void update(){
            if(expanding){scale+=0.05f;if(scale>=maxScale)expanding=false;}
            else{scale-=0.05f;if(scale<=1f){expanding=true;cycles--;if(cycles<=0)done=true;}}
        }
    }

    private final List<CardAnimation> cardAnims=new ArrayList<>();
    private final List<TextAnimation> textAnims=new ArrayList<>();
    private final List<PulseAnimation> pulseAnims=new ArrayList<>();
    private final List<Runnable> repaintListeners=new ArrayList<>();
    private javax.swing.Timer loop;

    private void startLoop(){
        loop=new javax.swing.Timer(16,e->tick());loop.start();
    }

    private void tick(){
        boolean dirty=false;
        cardAnims.removeIf(a->{a.update();return a.done;});
        textAnims.removeIf(a->{a.update();return a.done;});
        pulseAnims.removeIf(a->{a.update();return a.done;});
        if(!cardAnims.isEmpty()||!textAnims.isEmpty()||!pulseAnims.isEmpty())dirty=true;
        if(dirty)repaintListeners.forEach(Runnable::run);
    }

    public CardAnimation animateCardDeal(float sx,float sy,float tx,float ty,Runnable onComplete){
        CardAnimation a=new CardAnimation(sx,sy,tx,ty);a.onComplete=onComplete;cardAnims.add(a);return a;
    }
    public void animateCardFlip(CardAnimation a){a.flipping=true;a.flipProgress=0f;}
    public void floatText(String t,float x,float y,Color c,Font f){textAnims.add(new TextAnimation(t,x,y,c,f));}
    public PulseAnimation pulse(float max,int cycles){PulseAnimation p=new PulseAnimation(max,cycles);pulseAnims.add(p);return p;}
    public void addRepaintListener(Runnable r){repaintListeners.add(r);}
    public void removeRepaintListener(Runnable r){repaintListeners.remove(r);}
    public List<CardAnimation> getCardAnimations(){return cardAnims;}
    public List<TextAnimation> getTextAnimations(){return textAnims;}
    public boolean isAnimating(){return!cardAnims.isEmpty();}
    public void clearAll(){cardAnims.clear();textAnims.clear();pulseAnims.clear();}

    public void drawTextAnimations(Graphics2D g2){
        for(TextAnimation ta:new ArrayList<>(textAnims)){
            Composite orig=g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,ta.alpha));
            g2.setFont(ta.font);g2.setColor(ta.color);
            FontMetrics fm=g2.getFontMetrics();
            g2.drawString(ta.text,ta.x-fm.stringWidth(ta.text)/2f,ta.y);
            g2.setComposite(orig);
        }
    }
}
