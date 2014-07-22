/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.task;

import net.galaxygaming.dispenser.game.Game;

/**
 * @author t7seven7t
 */
public abstract class CountdownTask extends GameRunnable {

    private final int duration;
    private int countdown;
    private final String message;
    private final Game game;
    /**
     * Create a new countdown task
     * @param game game
     * @param duration in seconds
     * @param message message to broadcast during countdown
     */
    public CountdownTask(Game game, int duration, String message) {
        this.countdown = duration;
        this.duration = duration;
        this.game = game;
        this.message = message;
        this.runTaskTimer(0L, 20L);
    }
    
    @Override
    public void run() {
        if ((countdown % 60 == 0 && countdown != duration) 
                || (countdown < 60 && countdown % 30 == 0)
                || (countdown <= 5)) {
            game.broadcast(message, countdown);
        }
        
        countdown--;
        
        if (countdown <= 0) {
            new GameRunnable() {
                @Override
                public void run() {
                    done();
                }
            }.runTaskLater(20L);
            this.cancel();
        }
    }
    
    public abstract void done();

}
