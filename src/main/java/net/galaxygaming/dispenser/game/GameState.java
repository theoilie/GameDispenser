package net.galaxygaming.dispenser.game;

import org.bukkit.ChatColor;

/**
 * Used for keeping track of the game's progression
 */
public class GameState {
    
    public static final GameState   
        EDITING     = new GameState(-10, ChatColor.DARK_RED + "In Construction"),
        INACTIVE    = new GameState(0, ChatColor.YELLOW + "Idle"), 
        LOBBY       = new GameState(1, ChatColor.GREEN + "Open"), 
        STARTING    = new GameState(2, ChatColor.GREEN + "Open"),
        GRACE       = new GameState(3, ChatColor.BLUE + "In Game"),
        ACTIVE      = new GameState(10, ChatColor.BLUE + "In Game");
    
    private final int ordinal;
    private final String name;
    
    public GameState(int ordinal, String name) {
        this.ordinal = ordinal;
        this.name = name;
    }
    
    /**
     * Returns the ordinal of this GameState (its
     * position in the progression of a game)
     * @return the ordinal of this state
     */
    public int ordinal() {
        return this.ordinal;
    }
    
    /**
     * Returns a more meaningful name
     * to display on signs, etc...
     * @return the name of the state
     */
    public String getFancyName() {
        return name;
    }
}