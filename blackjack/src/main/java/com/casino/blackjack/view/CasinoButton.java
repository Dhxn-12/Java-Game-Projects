package com.casino.blackjack.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A polished casino-themed button with hover/press animation and gold glow.
 */
public class CasinoButton extends JButton {

    public enum Style { PRIMARY, DANGER, SUCCESS, NEUTRAL, GOLD, CHIP }

    private Style style;
    private boolean hovered=false, pressed=false;
    private float glowAlpha=0f;

    public CasinoButton(String text, Style style){super(text);this.style=style;setup();}
    public CasinoButton(String text){this(text,Style.PRIMARY);}

    private void setup(){
        setOpaque(false);setContentAreaFilled(false);setBorderPainted(false);setFocusPainted(false);
        setFont(CasinoTheme.FONT_BUTTON);setForeground(CasinoTheme.TEXT_PRIMARY);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(120,CasinoTheme.BTN_HEIGHT));
        addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){hovered=true;glowAlpha=1f;repaint();}
            @Override public void mouseExited(MouseEvent e){hovered=false;glowAlpha=0f;repaint();}
            @Override public void mousePressed(MouseEvent e){pressed=true;repaint();}
            @Override public void mouseReleased(MouseEvent e){pressed=false;repaint();}
        });
    }

    @Override protected void paintComponent(Graphics g){
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        int w=getWidth(),h=getHeight(),yo=pressed?2:0;
        if(!pressed){g2.setColor(new Color(0,0,0,60));g2.fill(new RoundRectangle2D.Float(2,4,w-2,h-2,CasinoTheme.BTN_ARC,CasinoTheme.BTN_ARC));}
        Color top=hovered?getHoverColor():getBaseColor();
        Color bot=CasinoTheme.darken(top,0.25f);
        g2.setPaint(new GradientPaint(0,yo,top,0,h+yo,bot));
        g2.fill(new RoundRectangle2D.Float(0,yo,w,h-2,CasinoTheme.BTN_ARC,CasinoTheme.BTN_ARC));
        if(glowAlpha>0){
            g2.setColor(CasinoTheme.withAlpha(CasinoTheme.GOLD,(int)(80*glowAlpha)));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new RoundRectangle2D.Float(1,yo+1,w-2,h-4,CasinoTheme.BTN_ARC,CasinoTheme.BTN_ARC));
        }
        g2.setColor(new Color(255,255,255,40));g2.setStroke(new BasicStroke(1f));
        g2.drawLine(CasinoTheme.BTN_ARC/2,yo+1,w-CasinoTheme.BTN_ARC/2,yo+1);
        g2.setFont(getFont());
        FontMetrics fm=g2.getFontMetrics();
        int tx=(w-fm.stringWidth(getText()))/2,ty=(h-fm.getHeight())/2+fm.getAscent()+yo;
        g2.setColor(new Color(0,0,0,80));g2.drawString(getText(),tx+1,ty+1);
        g2.setColor(isEnabled()?getForeground():CasinoTheme.withAlpha(getForeground(),100));
        g2.drawString(getText(),tx,ty);
        g2.dispose();
    }

    private Color getBaseColor(){return switch(style){
        case PRIMARY->CasinoTheme.BTN_PRIMARY; case DANGER->CasinoTheme.BTN_DANGER;
        case SUCCESS->CasinoTheme.BTN_SUCCESS; case NEUTRAL->CasinoTheme.BTN_NEUTRAL;
        case GOLD->new Color(0xB8860B); case CHIP->CasinoTheme.BTN_NEUTRAL;
    };}
    private Color getHoverColor(){return switch(style){
        case PRIMARY->CasinoTheme.BTN_PRIMARY_HOVER; case DANGER->CasinoTheme.BTN_DANGER_HOVER;
        case SUCCESS->CasinoTheme.BTN_SUCCESS_HOVER; case NEUTRAL->CasinoTheme.BTN_NEUTRAL_HOVER;
        case GOLD->new Color(0xDAA520); case CHIP->CasinoTheme.BTN_NEUTRAL_HOVER;
    };}

    public void setStyle(Style s){this.style=s;repaint();}
    @Override public boolean contains(int x,int y){
        return new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),CasinoTheme.BTN_ARC,CasinoTheme.BTN_ARC).contains(x,y);
    }
}
