package com.casino.blackjack.model;

/**
 * Observer interface — views implement this to react to game events.
 */
@FunctionalInterface
public interface GameObserver {
    void onGameEvent(GameEvent event, Object payload);
}
