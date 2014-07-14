/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import java.util.List;
import java.util.Set;


import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author t7seven7t
 */
class PrefixedReflectCommand extends ReflectCommand implements CommandExecutor {

    private final Set<Command> subCommands;
    
    public PrefixedReflectCommand(String prefix) {
        super(prefix);
        this.setDescription(prefix + " base command");
        this.setExecutor(this);
        this.subCommands = Sets.newHashSet();
    }
    
    void addExecutor(Command command) {
        subCommands.add(command);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command prefixCommand, String prefix, String[] args) {
        List<String> argsList = Lists.newArrayList();
        
        if (args.length > 0) {
            String commandName = args[0];
            for (int i = 1; i < args.length; i++) {
                argsList.add(args[i]);
            }
            
            for (Command command : subCommands) {
                if (commandName.equalsIgnoreCase(command.getName()) || command.getAliases().contains(commandName.toLowerCase())) {
                     return command.onCommand(sender, null, null, argsList.toArray(new String[0]));
                }
            }
        }
        
        return false;
    }

}
