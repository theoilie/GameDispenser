/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import net.galaxygaming.dispenser.game.GameType;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author t7seven7t
 */
public class CommandManager {
    
    /** Singleton instance */
    private static final CommandManager instance = new CommandManager();
    
    private final Map<GameType, Set<ReflectCommand>> commands;
    private CommandMap cmap;
    
    private CommandManager() {
        commands = Maps.newHashMap();
    }
    
    @Override
    public CommandManager clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    public void registerCommand(Command command, GameType type) {
        Set<ReflectCommand> commandSet = commands.get(type);
        if (commandSet == null) {
            commandSet = Sets.newHashSet();
            commands.put(type, commandSet);
        }
        
        ReflectCommand cmd;
        
        if (command.hasPrefix()) {
            PrefixedReflectCommand prefixedCommand = null;
            
            for (ReflectCommand reflectCommand : commandSet) {
                if (reflectCommand instanceof PrefixedReflectCommand 
                        && ((PrefixedReflectCommand) reflectCommand).getName()
                        .equalsIgnoreCase(command.getPrefix())) 
                {
                    prefixedCommand = (PrefixedReflectCommand) reflectCommand;
                    break;
                }
            }
            
            if (prefixedCommand == null) {
                prefixedCommand = new PrefixedReflectCommand(command.getPrefix());
                commandSet.add(prefixedCommand);
            }
            
            prefixedCommand.addExecutor(command);
            cmd = prefixedCommand;
        } else {
            cmd = new ReflectCommand(command, command);
            commandSet.add(cmd);
        }
        
        if (getCommandMap().getCommand(cmd.getName()) == null)
            getCommandMap().register("", cmd);
    }
    
    CommandMap getCommandMap() {
        if (cmap == null) {
            try {
                final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                cmap = (CommandMap) f.get(Bukkit.getServer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return cmap;
    }
    
    public void unregisterCommands(GameType type) {
        Set<ReflectCommand> commandSet = commands.get(type);
        if (commandSet != null) {
            for (ReflectCommand command : commandSet) {
                command.unregister(getCommandMap());
            }
        }
        commands.remove(type);
    }
    
    public void unregisterAll() {
        for (Set<ReflectCommand> commandSet : commands.values()) {
            for (ReflectCommand command : commandSet) {
                command.unregister(getCommandMap());
            }
        }
        commands.clear();
    }
    
    public static CommandManager getInstance() {
        return instance;
    }
}