package com.casino.blackjack.model;

/**
 * All possible states in a round of Blackjack (MVC state machine).
 */
public enum GameState {
    MAIN_MENU, SETTINGS, LEADERBOARD, STATISTICS, TUTORIAL,
    PLACING_BET, DEALING, INSURANCE_OFFER,
    PLAYER_TURN, PLAYER_TURN_SPLIT, DEALER_TURN,
    ROUND_OVER, GAME_OVER
}
