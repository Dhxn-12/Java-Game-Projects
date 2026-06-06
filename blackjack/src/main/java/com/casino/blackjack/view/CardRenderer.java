package com.casino.blackjack.view;

import com.casino.blackjack.model.Card;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Renders playing cards entirely in code – no image assets required.
 */
public class CardRenderer {
    private static final Color RED   =new Color(0xCC1111);
    private static final Color BLACK =new Color(0x1A1A1A);
    private static final Color BG    =new Color(0xFAF7F0);
    private static final Color BORDER=new Color(0xD4C8A8);
    private static final Color BACK_BG =new Color(0x1A3A6E);

    private CardRenderer(){}

    public static void drawCard(Graphics2D g2,Card card,int x,int y,int w,int h,float alpha){
        drawCard(g2,card,x,y,w,h,alpha,0);
    }

    public static void drawCard(Graphics2D g2,Card card,int x,int y,int w,int h,float alpha,double rotateDeg){
        Composite orig=g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        if(rotateDeg!=0){
            Graphics2D r=(Graphics2D)g2.create();
            r.translate(x+w/2.0,y+h/2.0);r.rotate(Math.toRadians(rotateDeg));r.translate(-w/2.0,-h/2.0);
            renderCard(r,card,0,0,w,h);r.dispose();
        }else{renderCard(g2,card,x,y,w,h);}
        g2.setComposite(orig);
    }

    private static void renderCard(Graphics2D g2,Card card,int x,int y,int w,int h){
        int arc=Math.max(8,w/8);
        // Drop shadow
        g2.setColor(new Color(0,0,0,50));
        g2.fill(new RoundRectangle2D.Float(x+3,y+4,w,h,arc,arc));
        if(!card.isFaceUp()){drawBack(g2,x,y,w,h,arc);return;}
        // Face bg
        g2.setPaint(new GradientPaint(x,y,BG,x,y+h,new Color(0xEDE8DC)));
        g2.fill(new RoundRectangle2D.Float(x,y,w,h,arc,arc));
        g2.setColor(BORDER);g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Float(x,y,w,h,arc,arc));

        Color tc=card.isRedSuit()?RED:BLACK;
        String rank=card.getRank().getSymbol(),suit=card.getSuit().getSymbol();
        Font rf=new Font("Georgia",Font.BOLD,Math.max(10,w/5));
        Font sf=new Font("Segoe UI Symbol",Font.PLAIN,Math.max(12,w/4));
        Font cf=new Font("Segoe UI Symbol",Font.PLAIN,Math.max(20,w/2));

        // Top-left corner
        g2.setColor(tc);g2.setFont(rf);
        g2.drawString(rank,x+5,y+5+g2.getFontMetrics().getAscent());
        g2.setFont(sf);
        FontMetrics rfm=g2.getFontMetrics(rf);
        g2.drawString(suit,x+5,y+5+rfm.getHeight()+g2.getFontMetrics().getAscent()-2);

        // Bottom-right (rotated)
        Graphics2D rot=(Graphics2D)g2.create();
        rot.translate(x+w-5,y+h-5);rot.rotate(Math.PI);
        rot.setColor(tc);rot.setFont(rf);
        rot.drawString(rank,0,g2.getFontMetrics(rf).getAscent());
        rot.setFont(sf);rot.drawString(suit,0,rfm.getHeight()+g2.getFontMetrics(sf).getAscent()-2);
        rot.dispose();

        // Centre suit
        g2.setFont(cf);g2.setColor(tc);
        FontMetrics fm=g2.getFontMetrics();
        g2.drawString(suit,x+(w-fm.stringWidth(suit))/2,y+(h+fm.getAscent())/2-fm.getDescent());

        // Face-card inner border
        if(card.isFaceCard()){
            g2.setColor(CasinoTheme.withAlpha(tc,30));g2.setStroke(new BasicStroke(2f));
            g2.draw(new RoundRectangle2D.Float(x+8,y+8,w-16,h-16,arc-2,arc-2));
        }
    }

    private static void drawBack(Graphics2D g2,int x,int y,int w,int h,int arc){
        g2.setColor(BACK_BG);
        g2.fill(new RoundRectangle2D.Float(x,y,w,h,arc,arc));
        // Diamond pattern
        g2.setColor(new Color(0x0D2454));g2.setStroke(new BasicStroke(1f));
        int sp=Math.max(8,w/8);
        for(int dx=x;dx<x+w+sp;dx+=sp)
            for(int dy=y;dy<y+h+sp;dy+=sp){
                int[]px={dx,dx+sp/2,dx+sp,dx+sp/2};int[]py={dy+sp/2,dy,dy+sp/2,dy+sp};
                g2.drawPolygon(px,py,4);
            }
        g2.setColor(new Color(255,255,255,48));g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(x+4,y+4,w-8,h-8,arc-2,arc-2));
        g2.setColor(BORDER);g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Float(x,y,w,h,arc,arc));
    }

    public static void drawEmptySlot(Graphics2D g2,int x,int y,int w,int h){
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        int arc=Math.max(8,w/8);
        g2.setColor(CasinoTheme.withAlpha(Color.WHITE,20));
        g2.fill(new RoundRectangle2D.Float(x,y,w,h,arc,arc));
        g2.setStroke(new BasicStroke(1.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1f,new float[]{6f,4f},0));
        g2.setColor(CasinoTheme.withAlpha(Color.WHITE,60));
        g2.draw(new RoundRectangle2D.Float(x,y,w,h,arc,arc));
    }
}
