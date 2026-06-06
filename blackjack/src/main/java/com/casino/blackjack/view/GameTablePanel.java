package com.casino.blackjack.view;

import com.casino.blackjack.manager.AchievementManager;

import com.casino.blackjack.controller.GameManager;
import com.casino.blackjack.manager.ChipManager;
import com.casino.blackjack.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Main casino table panel. Renders felt, cards, chips, buttons, scores and
 * floating result overlays.
 */
public class GameTablePanel extends JPanel implements GameObserver {

    private final GameManager gm = GameManager.getInstance();
    private final AnimationManager anim = AnimationManager.getInstance();

    private static final int PAD=20, DEALER_Y=175, PLAYER_Y=380;

    // Buttons
    private CasinoButton btnHit, btnStand, btnDouble, btnSplit, btnSurrender, btnDeal, btnClearBet, btnReBet, btnMaxBet;
    private CasinoButton[] chipBtns;
    private JPanel actionPanel, bettingPanel;

    // Labels
    private JLabel lblBalance, lblBet, lblHint, lblTimer, lblDealerScore, lblPlayerScore;
    private JLabel lblDeck, lblStatus;

    // Result overlay
    private String resultText=""; private Color resultColor=Color.WHITE; private float resultAlpha=0f;
    private Timer resultFadeTimer;

    public GameTablePanel(){
        setLayout(null);setBackground(CasinoTheme.TABLE_BG);
        buildUI();
        gm.addObserver(this);
        anim.addRepaintListener(this::repaint);
        updateButtonStates();
    }

    // ── Build UI ──────────────────────────────────────────────────────────────
    private void buildUI(){
        buildChipButtons();
        buildActionButtons();
        buildLabels();
        buildBettingPanel();
        buildActionPanel();
        assembleLayout();
    }

    private void buildChipButtons(){
        List<ChipManager.Chip> chips=ChipManager.CHIPS;
        chipBtns=new CasinoButton[chips.size()];
        for(int i=0;i<chips.size();i++){
            ChipManager.Chip chip=chips.get(i);
            int fi=i;
            chipBtns[i]=new CasinoButton(chip.label(),CasinoButton.Style.CHIP){
                @Override protected void paintComponent(Graphics g){paintChip(g,chip);}
            };
            chipBtns[i].setPreferredSize(new Dimension(CasinoTheme.CHIP_DIAMETER,CasinoTheme.CHIP_DIAMETER));
            chipBtns[i].setToolTipText("Add "+chip.label());
            ChipManager.Chip fc=chip;
            chipBtns[i].addActionListener(e->{gm.addChip(fc);updateBetDisplay();});
        }
    }

    private void paintChip(Graphics g, ChipManager.Chip chip){
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        int d=Math.min(getWidth(),getHeight())-4,x=(getWidth()-d)/2,y=(getHeight()-d)/2;
        Color chipColor;
        try{chipColor=Color.decode(chip.colorHex());}catch(Exception ex){chipColor=Color.RED;}
        g2.setColor(new Color(0,0,0,60));g2.fillOval(x+2,y+3,d,d);
        g2.setPaint(new GradientPaint(x,y,CasinoTheme.lighten(chipColor,0.3f),x,y+d,CasinoTheme.darken(chipColor,0.3f)));
        g2.fillOval(x,y,d,d);
        g2.setColor(CasinoTheme.withAlpha(Color.WHITE,80));
        g2.setStroke(new BasicStroke(3f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1f,new float[]{4f,4f},0));
        g2.drawOval(x+4,y+4,d-8,d-8);
        g2.setFont(CasinoTheme.FONT_CHIP);g2.setColor(Color.WHITE);
        FontMetrics fm=g2.getFontMetrics();
        g2.drawString(chip.label(),(getWidth()-fm.stringWidth(chip.label()))/2,(getHeight()+fm.getAscent())/2-fm.getDescent());
        g2.dispose();
    }

    private void buildActionButtons(){
        btnHit       =new CasinoButton("HIT",CasinoButton.Style.SUCCESS);
        btnStand     =new CasinoButton("STAND",CasinoButton.Style.DANGER);
        btnDouble    =new CasinoButton("DOUBLE",CasinoButton.Style.PRIMARY);
        btnSplit     =new CasinoButton("SPLIT",CasinoButton.Style.NEUTRAL);
        btnSurrender =new CasinoButton("SURRENDER",CasinoButton.Style.NEUTRAL);
        btnDeal      =new CasinoButton("DEAL ▶",CasinoButton.Style.GOLD);
        btnClearBet  =new CasinoButton("CLEAR",CasinoButton.Style.NEUTRAL);
        btnReBet     =new CasinoButton("RE-BET",CasinoButton.Style.NEUTRAL);
        btnMaxBet    =new CasinoButton("MAX BET",CasinoButton.Style.NEUTRAL);

        btnHit.addActionListener(e->gm.hit());
        btnStand.addActionListener(e->gm.stand());
        btnDouble.addActionListener(e->gm.doubleDown());
        btnSplit.addActionListener(e->gm.split());
        btnSurrender.addActionListener(e->gm.surrender());
        btnDeal.addActionListener(e->gm.startRound());
        btnClearBet.addActionListener(e->{gm.clearBet();updateBetDisplay();});
        btnReBet.addActionListener(e->{gm.reBet();updateBetDisplay();});
        btnMaxBet.addActionListener(e->{gm.maxBet();updateBetDisplay();});

        // Keyboard shortcuts (shown in tooltips)
        btnHit.setToolTipText("Hit [H]");btnStand.setToolTipText("Stand [S]");
        btnDouble.setToolTipText("Double Down [D]");btnSplit.setToolTipText("Split [P]");
        btnDeal.setToolTipText("Deal [Enter]");

        setupKeys();
    }

    private void setupKeys(){
        InputMap im=getInputMap(WHEN_IN_FOCUSED_WINDOW);ActionMap am=getActionMap();
        im.put(KeyStroke.getKeyStroke('h'),"hit");im.put(KeyStroke.getKeyStroke('H'),"hit");
        im.put(KeyStroke.getKeyStroke('s'),"stand");im.put(KeyStroke.getKeyStroke('S'),"stand");
        im.put(KeyStroke.getKeyStroke('d'),"double");im.put(KeyStroke.getKeyStroke('D'),"double");
        im.put(KeyStroke.getKeyStroke('p'),"split");im.put(KeyStroke.getKeyStroke('P'),"split");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"deal");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),"clear");
        am.put("hit",  new AbstractAction(){public void actionPerformed(ActionEvent e){gm.hit();}});
        am.put("stand",new AbstractAction(){public void actionPerformed(ActionEvent e){gm.stand();}});
        am.put("double",new AbstractAction(){public void actionPerformed(ActionEvent e){gm.doubleDown();}});
        am.put("split",new AbstractAction(){public void actionPerformed(ActionEvent e){gm.split();}});
        am.put("deal", new AbstractAction(){public void actionPerformed(ActionEvent e){gm.startRound();}});
        am.put("clear",new AbstractAction(){public void actionPerformed(ActionEvent e){gm.clearBet();}});
    }

    private void buildLabels(){
        lblBalance    =lbl("$1,000",CasinoTheme.FONT_TITLE,CasinoTheme.GOLD);
        lblBet        =lbl("BET: $0",CasinoTheme.FONT_BODY,CasinoTheme.TEXT_SECONDARY);
        lblHint       =lbl("",CasinoTheme.FONT_SMALL,CasinoTheme.TEXT_SECONDARY);
        lblTimer      =lbl("",CasinoTheme.FONT_TITLE,CasinoTheme.GOLD);
        lblDealerScore=lbl("",CasinoTheme.FONT_SUBTITLE,CasinoTheme.TEXT_PRIMARY);
        lblPlayerScore=lbl("",CasinoTheme.FONT_SUBTITLE,CasinoTheme.TEXT_PRIMARY);
        lblDeck       =lbl("",CasinoTheme.FONT_SMALL,CasinoTheme.TEXT_SECONDARY);
        lblStatus     =lbl("",CasinoTheme.FONT_BODY,CasinoTheme.TEXT_PRIMARY);
        lblHint.setHorizontalAlignment(SwingConstants.CENTER);
        lblDealerScore.setHorizontalAlignment(SwingConstants.CENTER);
        lblPlayerScore.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private JLabel lbl(String t,Font f,Color c){JLabel l=new JLabel(t);l.setFont(f);l.setForeground(c);l.setOpaque(false);return l;}

    private void buildBettingPanel(){
        bettingPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,6,4));
        bettingPanel.setOpaque(false);
        for(CasinoButton cb:chipBtns)bettingPanel.add(cb);
        bettingPanel.add(Box.createHorizontalStrut(10));
        bettingPanel.add(btnClearBet);bettingPanel.add(btnReBet);bettingPanel.add(btnMaxBet);
        bettingPanel.add(Box.createHorizontalStrut(10));
        bettingPanel.add(btnDeal);
    }

    private void buildActionPanel(){
        actionPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,10,6));
        actionPanel.setOpaque(false);
        actionPanel.add(btnHit);actionPanel.add(btnStand);
        actionPanel.add(btnDouble);actionPanel.add(btnSplit);actionPanel.add(btnSurrender);
    }

    private void assembleLayout(){
        removeAll();
        add(lblBalance);add(lblBet);add(lblHint);add(lblTimer);
        add(lblDealerScore);add(lblPlayerScore);add(lblDeck);add(lblStatus);
        add(bettingPanel);add(actionPanel);
    }

    @Override public void doLayout(){
        int w=getWidth(),h=getHeight();if(w==0)return;
        lblBalance.setBounds(PAD,PAD,220,36);
        lblTimer.setBounds(w/2-50,PAD,100,36);
        lblDeck.setBounds(w-180,PAD,170,20);
        lblBet.setBounds(PAD,h/2-20,200,24);
        lblStatus.setBounds(w/2-150,h-145,300,22);
        lblHint.setBounds(PAD,h-125,w-PAD*2,20);
        lblDealerScore.setBounds(w/2-60,DEALER_Y-42,120,24);
        lblPlayerScore.setBounds(w/2-60,PLAYER_Y+100,120,24);
        Dimension bd=bettingPanel.getPreferredSize();
        bettingPanel.setBounds((w-bd.width)/2,h-bd.height-8,bd.width,bd.height);
        Dimension ad=actionPanel.getPreferredSize();
        actionPanel.setBounds((w-ad.width)/2,h-bd.height-ad.height-18,ad.width,ad.height);
    }

    // ── PAINTING ─────────────────────────────────────────────────────────────
    @Override protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        int w=getWidth(),h=getHeight();
        drawFelt(g2,w,h);
        drawRail(g2,w,h);
        drawTableArt(g2,w,h);
        drawZoneLabels(g2,w,h);
        drawDealerCards(g2,w,h);
        drawPlayerCards(g2,w,h);
        drawBetStack(g2,w,h);
        drawResultOverlay(g2,w,h);
        anim.drawTextAnimations(g2);
        g2.dispose();
    }

    private void drawFelt(Graphics2D g2,int w,int h){
        g2.setPaint(CasinoTheme.tableGradient(w,h));g2.fillRect(0,0,w,h);
        g2.setColor(CasinoTheme.withAlpha(Color.BLACK,8));
        for(int x=0;x<w;x+=4)for(int y=0;y<h;y+=4)g2.fillOval(x,y,1,1);
    }

    private void drawRail(Graphics2D g2,int w,int h){
        int rh=68;
        g2.setPaint(new GradientPaint(0,h-rh,CasinoTheme.TABLE_RAIL,0,h,CasinoTheme.darken(CasinoTheme.TABLE_RAIL,0.4f)));
        g2.fillRect(0,h-rh,w,rh);
        g2.setColor(CasinoTheme.withAlpha(Color.WHITE,30));g2.fillRect(0,h-rh,w,2);
        g2.setColor(CasinoTheme.withAlpha(CasinoTheme.GOLD,60));g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(10,10,w-20,h-rh-10,20,20);
    }

    private void drawTableArt(Graphics2D g2,int w,int h){
        g2.setFont(new Font("Georgia",Font.BOLD|Font.ITALIC,15));
        g2.setColor(CasinoTheme.withAlpha(CasinoTheme.GOLD,50));
        String name="✦ CLAUDE'S CASINO ✦";
        FontMetrics fm=g2.getFontMetrics();
        g2.drawString(name,(w-fm.stringWidth(name))/2,100);
        g2.setFont(new Font("Georgia",Font.BOLD,10));
        g2.setColor(CasinoTheme.withAlpha(CasinoTheme.TEXT_SECONDARY,50));
        String r1="BLACKJACK PAYS 3 TO 2",r2="DEALER MUST STAND ON ALL 17s";
        fm=g2.getFontMetrics();
        g2.drawString(r1,(w-fm.stringWidth(r1))/2,118);
        g2.drawString(r2,(w-fm.stringWidth(r2))/2,132);
        // Divider line between zones
        g2.setColor(CasinoTheme.withAlpha(CasinoTheme.GOLD,25));
        g2.setStroke(new BasicStroke(1f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1f,new float[]{8f,6f},0));
        g2.drawLine(60,(DEALER_Y+PLAYER_Y)/2,w-60,(DEALER_Y+PLAYER_Y)/2);
    }

    private void drawZoneLabels(Graphics2D g2,int w,int h){
        g2.setFont(new Font("Georgia",Font.BOLD,11));
        g2.setColor(CasinoTheme.withAlpha(CasinoTheme.GOLD,120));
        FontMetrics fm=g2.getFontMetrics();
        String dl="DEALER";g2.drawString(dl,(w-fm.stringWidth(dl))/2,DEALER_Y-52);
        String pl="PLAYER";g2.drawString(pl,(w-fm.stringWidth(pl))/2,PLAYER_Y-52);
    }

    private void drawDealerCards(Graphics2D g2,int w,int h){
        if(gm.getDealer()==null)return;
        var cards=gm.getDealer().getHand().getCards();
        drawHandCards(g2,cards,w/2,DEALER_Y);
        if(!cards.isEmpty()){
            boolean hasHidden=cards.stream().anyMatch(c->!c.isFaceUp());
            lblDealerScore.setText(hasHidden?"?":String.valueOf(gm.getDealer().getHandValue()));
        }else lblDealerScore.setText("");
    }

    private void drawPlayerCards(Graphics2D g2,int w,int h){
        if(gm.getPlayer()==null){lblPlayerScore.setText("");return;}
        var hands=gm.getPlayer().getHands();
        if(hands.isEmpty()){lblPlayerScore.setText("");return;}
        if(hands.size()==1){
            drawHandCards(g2,hands.get(0).getCards(),w/2,PLAYER_Y);
            int v=hands.get(0).getValue();
            String vs=v>0?String.valueOf(v):"";
            if(hands.get(0).isBusted())lblPlayerScore.setForeground(CasinoTheme.LOSE_COLOR);
            else if(v==21)lblPlayerScore.setForeground(CasinoTheme.WIN_COLOR);
            else lblPlayerScore.setForeground(CasinoTheme.TEXT_PRIMARY);
            lblPlayerScore.setText(vs);
        }else{
            int sp=w/4,ai=gm.getPlayer().getActiveHandIndex();
            for(int i=0;i<hands.size();i++){
                int hx=w/2-sp/2+(i*sp);
                if(i==ai){
                    g2.setColor(CasinoTheme.withAlpha(CasinoTheme.GOLD,35));
                    g2.fillRoundRect(hx-CasinoTheme.CARD_WIDTH/2-10,PLAYER_Y-CasinoTheme.CARD_HEIGHT/2-10,
                            CasinoTheme.CARD_WIDTH+70,CasinoTheme.CARD_HEIGHT+20,12,12);
                }
                drawHandCards(g2,hands.get(i).getCards(),hx,PLAYER_Y);
            }
            Hand ah=gm.getPlayer().getActiveHand();
            if(ah!=null)lblPlayerScore.setText(String.valueOf(ah.getValue()));
        }
    }

    private void drawHandCards(Graphics2D g2,java.util.List<Card> cards,int cx,int cy){
        if(cards.isEmpty())return;
        int cw=CasinoTheme.CARD_WIDTH,ch=CasinoTheme.CARD_HEIGHT;
        int overlap=Math.min(32,(cw*3)/(cards.size()+1));
        int totalW=cw+overlap*(cards.size()-1);
        int sx=cx-totalW/2;
        for(int i=0;i<cards.size();i++)
            CardRenderer.drawCard(g2,cards.get(i),sx+i*overlap,cy-ch/2,cw,ch,1f);
    }

    private void drawBetStack(Graphics2D g2,int w,int h){
        double bet=gm.getCurrentBet();
        if(bet<=0||gm.getState()!=GameState.PLACING_BET)return;
        int cx=w/2,cy=PLAYER_Y+CasinoTheme.CARD_HEIGHT/2+36;
        int layers=Math.min(8,(int)(Math.log10(Math.max(1,bet))*2));
        for(int i=layers;i>=0;i--){
            int yo=i*-3;
            g2.setColor(new Color(0,0,0,50));g2.fillOval(cx-22,cy-22+yo+2,44,44);
            g2.setColor(new Color(0xC41E3A));g2.fillOval(cx-22,cy-22+yo,44,44);
            g2.setColor(new Color(0xFF6677));g2.setStroke(new BasicStroke(1.5f));g2.drawOval(cx-22,cy-22+yo,44,44);
        }
        g2.setFont(CasinoTheme.FONT_CHIP);g2.setColor(Color.WHITE);
        String s="$"+(int)bet;FontMetrics fm=g2.getFontMetrics();
        g2.drawString(s,cx-fm.stringWidth(s)/2,cy+fm.getAscent()/2);
    }

    private void drawResultOverlay(Graphics2D g2,int w,int h){
        if(resultAlpha<=0)return;
        Composite orig=g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,resultAlpha));
        g2.setFont(new Font("Georgia",Font.BOLD,60));FontMetrics fm=g2.getFontMetrics();
        int tw=fm.stringWidth(resultText);
        g2.setColor(new Color(0,0,0,160));g2.drawString(resultText,(w-tw)/2+3,h/2+3);
        g2.setColor(resultColor);g2.drawString(resultText,(w-tw)/2,h/2);
        g2.setComposite(orig);
    }

    // ── OBSERVER ──────────────────────────────────────────────────────────────
    @Override public void onGameEvent(GameEvent event,Object payload){
        SwingUtilities.invokeLater(()->{
            switch(event){
                case STATE_CHANGED->onStateChanged((GameState)payload);
                case BALANCE_CHANGED->updateBalanceDisplay();
                case BET_PLACED,BET_CLEARED->updateBetDisplay();
                case CARD_DEALT,CARD_FLIPPED->repaint();
                case HAND_RESULT->showResult((Object[])payload);
                case HINT_UPDATED->lblHint.setText((String)payload);
                case SHOE_RESHUFFLED->lblDeck.setText("Shoe reshuffled");
                case TIMER_TICK->{
                    int t=(Integer)payload;
                    lblTimer.setText(t>0?String.valueOf(t):"");
                    lblTimer.setForeground(t<=5?Color.RED:CasinoTheme.GOLD);
                }
                case TIMER_EXPIRED->lblTimer.setText("");
                case ACHIEVEMENT_UNLOCKED->showAchievement((AchievementManager)payload);
                default->repaint();
            }
            if(gm.getDeck()!=null)lblDeck.setText(String.format("Shoe: %d/%d cards",
                    gm.getDeck().cardsRemaining(),gm.getDeck().totalCards()));
        });
    }

    private void onStateChanged(GameState s){updateButtonStates();repaint();}

    private void updateButtonStates(){
        GameState s=gm.getState();
        boolean betting=s==GameState.PLACING_BET;
        boolean playing=s==GameState.PLAYER_TURN||s==GameState.PLAYER_TURN_SPLIT;
        Hand ah=gm.getPlayer()!=null?gm.getPlayer().getActiveHand():null;
        boolean canSplit=playing&&ah!=null&&ah.canSplit()&&gm.getPlayer().canBet(ah.getBet());
        boolean canDouble=playing&&s==GameState.PLAYER_TURN&&ah!=null&&ah.canDoubleDown()&&gm.getPlayer().canBet(ah.getBet());
        boolean canSurr=playing&&s==GameState.PLAYER_TURN&&ah!=null&&ah.getCardCount()==2&&gm.getSettings().isSurrenderAllowed();

        for(CasinoButton cb:chipBtns)cb.setEnabled(betting);
        btnClearBet.setEnabled(betting);btnReBet.setEnabled(betting);btnMaxBet.setEnabled(betting);
        btnDeal.setEnabled(betting&&gm.isBetValid());
        btnHit.setEnabled(playing);btnStand.setEnabled(playing);
        btnDouble.setEnabled(canDouble);btnSplit.setEnabled(canSplit);btnSurrender.setEnabled(canSurr);

        bettingPanel.setVisible(betting);
        actionPanel.setVisible(playing);
    }

    private void updateBalanceDisplay(){
        if(gm.getPlayer()!=null)lblBalance.setText(String.format("$%,.0f",gm.getPlayer().getBalance()));
    }
    private void updateBetDisplay(){
        lblBet.setText(String.format("BET: $%.0f",gm.getCurrentBet()));
        btnDeal.setEnabled(gm.isBetValid());
    }

    private void showResult(Object[] p){
        if(p==null||p.length<3)return;
        RoundResult result=(RoundResult)p[1]; double net=(Double)p[2];
        resultText=result.getLabel(); resultColor=new Color(result.getColor()); resultAlpha=1f;
        int cx=getWidth()/2,cy=PLAYER_Y-30;
        String ns=(net>=0?"+":"")+(String.format("$%.0f",net));
        Color nc=net>=0?CasinoTheme.WIN_COLOR:CasinoTheme.LOSE_COLOR;
        anim.floatText(ns,cx,cy,nc,new Font("Georgia",Font.BOLD,24));
        if(resultFadeTimer!=null)resultFadeTimer.stop();
        resultFadeTimer=new Timer(40,null);
        resultFadeTimer.addActionListener(ev->{resultAlpha-=0.012f;if(resultAlpha<=0){resultAlpha=0;resultFadeTimer.stop();}repaint();});
        resultFadeTimer.setInitialDelay(1400);resultFadeTimer.start();
    }

    private void showAchievement(Object o){ /* handled by MainWindow toast */ }
}

// Import fix for AchievementManager reference
