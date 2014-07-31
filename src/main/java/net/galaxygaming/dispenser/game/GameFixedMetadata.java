package net.galaxygaming.dispenser.game;

import org.bukkit.metadata.FixedMetadataValue;

public class GameFixedMetadata extends FixedMetadataValue implements GameMetadata {
    
    public GameFixedMetadata(GameBase game, Object value) {
        super(game.fakePlugin, value);
    }
}