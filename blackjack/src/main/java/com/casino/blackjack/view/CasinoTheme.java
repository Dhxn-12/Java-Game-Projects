package com.casino.blackjack.view;

import java.awt.*;

/**
 * Central theme registry – colors, fonts, dimensions for the casino UI.
 * Supports 4 visual themes via Factory-style static methods.
 */
public class CasinoTheme {

    public enum Theme { CLASSIC_GREEN, MIDNIGHT_BLUE, ROYAL_RED, DESERT_GOLD }

    private static Theme activeTheme = Theme.CLASSIC_GREEN;

    public static Color TABLE_BG, TABLE_FELT, TABLE_TRIM, TABLE_RAIL, GOLD;
    public static Color TEXT_PRIMARY, TEXT_SECONDARY, TEXT_ACCENT;
    public static Color BTN_PRIMARY, BTN_PRIMARY_HOVER, BTN_DANGER, BTN_DANGER_HOVER;
    public static Color BTN_SUCCESS, BTN_SUCCESS_HOVER, BTN_NEUTRAL, BTN_NEUTRAL_HOVER;
    public static Color CARD_BG, CARD_BORDER, OVERLAY_BG;
    public static Color WIN_COLOR, LOSE_COLOR, PUSH_COLOR, CHIP_SHADOW, PANEL_BG, PANEL_BORDER;

    public static Font FONT_DISPLAY, FONT_TITLE, FONT_SUBTITLE, FONT_BODY, FONT_SMALL;
    public static Font FONT_CARD_RANK, FONT_CARD_SUIT, FONT_BUTTON, FONT_CHIP, FONT_MONO;

    public static final int CARD_WIDTH=90, CARD_HEIGHT=130, CARD_ARC=12;
    public static final int CHIP_DIAMETER=52, BTN_HEIGHT=44, BTN_ARC=8;

    static { applyTheme(Theme.CLASSIC_GREEN); }

    public static void applyTheme(Theme t){
        activeTheme=t; loadFonts();
        switch(t){
            case CLASSIC_GREEN -> classicGreen();
            case MIDNIGHT_BLUE -> midnightBlue();
            case ROYAL_RED     -> royalRed();
            case DESERT_GOLD   -> desertGold();
        }
    }
    public static Theme getActiveTheme(){return activeTheme;}

    private static void classicGreen(){
        TABLE_BG=new Color(0x1A2F1A); TABLE_FELT=new Color(0x1E4A1E); TABLE_TRIM=new Color(0x0D2210); TABLE_RAIL=new Color(0x8B4513);
        GOLD=new Color(0xD4AF37); TEXT_PRIMARY=new Color(0xF5F0E8); TEXT_SECONDARY=new Color(0xC8B89A); TEXT_ACCENT=new Color(0xD4AF37);
        BTN_PRIMARY=new Color(0x1A6B2A); BTN_PRIMARY_HOVER=new Color(0x228B35);
        BTN_DANGER=new Color(0xA0181A); BTN_DANGER_HOVER=new Color(0xC41E20);
        BTN_SUCCESS=new Color(0x1A6B2A); BTN_SUCCESS_HOVER=new Color(0x228B35);
        BTN_NEUTRAL=new Color(0x4A3728); BTN_NEUTRAL_HOVER=new Color(0x6B5040);
        CARD_BG=new Color(0xFAF7F0); CARD_BORDER=new Color(0xCCC0A0);
        OVERLAY_BG=new Color(0,0,0,153); WIN_COLOR=new Color(0xFFD700); LOSE_COLOR=new Color(0xFF4444); PUSH_COLOR=new Color(0xAAAAAA);
        CHIP_SHADOW=new Color(0,0,0,128); PANEL_BG=new Color(0x0D2210,false); PANEL_BORDER=new Color(0xD4AF37,false);
        PANEL_BG=new Color(13,34,16,200); PANEL_BORDER=new Color(212,175,55,136);
    }
    private static void midnightBlue(){
        TABLE_BG=new Color(0x0A0E2E); TABLE_FELT=new Color(0x0D1445); TABLE_TRIM=new Color(0x060A1E); TABLE_RAIL=new Color(0x1A237E);
        GOLD=new Color(0x7CB9E8); TEXT_PRIMARY=new Color(0xE8EAF6); TEXT_SECONDARY=new Color(0x9FA8DA); TEXT_ACCENT=new Color(0x7CB9E8);
        BTN_PRIMARY=new Color(0x1565C0); BTN_PRIMARY_HOVER=new Color(0x1976D2);
        BTN_DANGER=new Color(0xB71C1C); BTN_DANGER_HOVER=new Color(0xC62828);
        BTN_SUCCESS=new Color(0x1B5E20); BTN_SUCCESS_HOVER=new Color(0x2E7D32);
        BTN_NEUTRAL=new Color(0x283593); BTN_NEUTRAL_HOVER=new Color(0x3949AB);
        CARD_BG=new Color(0xF3F4FD); CARD_BORDER=new Color(0xC5CAE9);
        OVERLAY_BG=new Color(0,0,0,153); WIN_COLOR=new Color(0x7CB9E8); LOSE_COLOR=new Color(0xEF5350); PUSH_COLOR=new Color(0x90A4AE);
        CHIP_SHADOW=new Color(0,0,0,128); PANEL_BG=new Color(6,10,30,200); PANEL_BORDER=new Color(124,185,232,136);
    }
    private static void royalRed(){
        TABLE_BG=new Color(0x2A0808); TABLE_FELT=new Color(0x4A0E0E); TABLE_TRIM=new Color(0x1A0505); TABLE_RAIL=new Color(0x6D1F1F);
        GOLD=new Color(0xFFB300); TEXT_PRIMARY=new Color(0xFFF8F0); TEXT_SECONDARY=new Color(0xE0B0B0); TEXT_ACCENT=new Color(0xFFB300);
        BTN_PRIMARY=new Color(0x8B0000); BTN_PRIMARY_HOVER=new Color(0xA00000);
        BTN_DANGER=new Color(0x5D4037); BTN_DANGER_HOVER=new Color(0x6D4C41);
        BTN_SUCCESS=new Color(0x1B5E20); BTN_SUCCESS_HOVER=new Color(0x2E7D32);
        BTN_NEUTRAL=new Color(0x4E342E); BTN_NEUTRAL_HOVER=new Color(0x5D4037);
        CARD_BG=new Color(0xFFFAF5); CARD_BORDER=new Color(0xD7B8A0);
        OVERLAY_BG=new Color(0,0,0,153); WIN_COLOR=new Color(0xFFB300); LOSE_COLOR=new Color(0xFF1744); PUSH_COLOR=new Color(0xBDBDBD);
        CHIP_SHADOW=new Color(0,0,0,128); PANEL_BG=new Color(26,5,5,200); PANEL_BORDER=new Color(255,179,0,136);
    }
    private static void desertGold(){
        TABLE_BG=new Color(0x2A1F0A); TABLE_FELT=new Color(0x3D2C0F); TABLE_TRIM=new Color(0x1A1005); TABLE_RAIL=new Color(0x8B6914);
        GOLD=new Color(0xF5C518); TEXT_PRIMARY=new Color(0xFFF9E6); TEXT_SECONDARY=new Color(0xE8D5A3); TEXT_ACCENT=new Color(0xF5C518);
        BTN_PRIMARY=new Color(0x7A5C00); BTN_PRIMARY_HOVER=new Color(0x9A7400);
        BTN_DANGER=new Color(0x8B2500); BTN_DANGER_HOVER=new Color(0xA83000);
        BTN_SUCCESS=new Color(0x2D5A00); BTN_SUCCESS_HOVER=new Color(0x3D7A00);
        BTN_NEUTRAL=new Color(0x5C4200); BTN_NEUTRAL_HOVER=new Color(0x7A5800);
        CARD_BG=new Color(0xFFFBF0); CARD_BORDER=new Color(0xD4B483);
        OVERLAY_BG=new Color(0,0,0,153); WIN_COLOR=new Color(0xF5C518); LOSE_COLOR=new Color(0xFF5722); PUSH_COLOR=new Color(0xBCAAA4);
        CHIP_SHADOW=new Color(0,0,0,128); PANEL_BG=new Color(26,16,5,200); PANEL_BORDER=new Color(245,197,24,136);
    }

    private static void loadFonts(){
        FONT_DISPLAY  =new Font("Georgia",Font.BOLD,52);
        FONT_TITLE    =new Font("Georgia",Font.BOLD,28);
        FONT_SUBTITLE =new Font("Georgia",Font.ITALIC,18);
        FONT_BODY     =new Font("Segoe UI",Font.PLAIN,14);
        FONT_SMALL    =new Font("Segoe UI",Font.PLAIN,11);
        FONT_CARD_RANK=new Font("Georgia",Font.BOLD,22);
        FONT_CARD_SUIT=new Font("Segoe UI Symbol",Font.PLAIN,26);
        FONT_BUTTON   =new Font("Segoe UI",Font.BOLD,13);
        FONT_CHIP     =new Font("Segoe UI",Font.BOLD,11);
        FONT_MONO     =new Font("Consolas",Font.PLAIN,12);
    }

    public static Color withAlpha(Color c,int a){return new Color(c.getRed(),c.getGreen(),c.getBlue(),a);}
    public static Color darken(Color c,float f){
        return new Color(Math.max(0,(int)(c.getRed()*(1-f))),Math.max(0,(int)(c.getGreen()*(1-f))),Math.max(0,(int)(c.getBlue()*(1-f))));
    }
    public static Color lighten(Color c,float f){
        return new Color(Math.min(255,(int)(c.getRed()+(255-c.getRed())*f)),Math.min(255,(int)(c.getGreen()+(255-c.getGreen())*f)),Math.min(255,(int)(c.getBlue()+(255-c.getBlue())*f)));
    }
    public static GradientPaint tableGradient(int w,int h){
        return new GradientPaint(0,0,darken(TABLE_FELT,0.3f),w,h,lighten(TABLE_FELT,0.1f));
    }
}
