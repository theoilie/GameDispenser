/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.permissions.Permission;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.game.GameType;

/**
 * @author t7seven7t
 */
class ListCommand extends Command {

    public ListCommand() {
        this.prefix = "gd";
        this.name = "list";
        this.description = "Shows a list of games or game types";
        this.permission = new Permission("gamedispenser.command.list");
    }
    
    @Override
    public void perform() {
        Map<GameType, List<Game>> map = Maps.newHashMap();
        for (GameType gameType : GameManager.getInstance().getGameTypes()) {
            map.put(gameType, Lists.<Game>newArrayList());
        }
        
        for (Game game : GameManager.getInstance().getGames()) {
            map.get(game.getType()).add(game);
        }
        
        for (Entry<GameType, List<Game>> entry : map.entrySet()) {
            StringBuilder result = new StringBuilder();
            result.append(entry.getKey().toString() + ": ");
            for (Game game : entry.getValue()) {
                result.append(game.getName() + ", ");
            }
            result.deleteCharAt(result.length() - 1);
            sendMessage(result.toString());
        }
    }
}