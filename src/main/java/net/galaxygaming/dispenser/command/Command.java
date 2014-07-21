/**
 * Copyright (C) 2014 t7seven7t
 */
package net.galaxygaming.dispenser.command;

import java.util.List;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.MessagesResource;
import net.galaxygaming.util.FormatUtil;
import net.galaxygaming.util.PermissionUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.google.common.collect.Lists;

/**
 * @author t7seven7t
 */
public abstract class Command implements CommandExecutor {
    
    private final GameDispenser plugin = GameDispenser.getInstance();
    
    protected CommandSender sender;
    protected Player player;
    protected String args[];
    
    protected String name = "";
    protected String description = "";
    protected Permission permission = null;
    
    protected boolean mustBePlayer;
    protected List<String> requiredArgs = Lists.newArrayList();
    protected List<String> optionalArgs = Lists.newArrayList();
    protected List<String> aliases = Lists.newArrayList();
    
    protected String prefix = "";
    
    protected MessagesResource messages = new MessagesResource(getClass().getClassLoader());
    
    public abstract void perform();
    
    public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        this.sender = sender;
        this.args = args;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        
        if (mustBePlayer && !isPlayer()) {
            error(messages.getMessage(CommandMessage.MUST_BE_PLAYER));
            return true;
        }
        
        if (requiredArgs.size() > args.length) {
            error(messages.getMessage(CommandMessage.TOO_FEW_ARGS), getUsageTemplate(false));
            return true;
        }
        
        if (!hasPermission()) {
            error(messages.getMessage(CommandMessage.INSUFFICIENT_PERMISSION));
            return true;
        }
        
        perform();
        return true;
    }
    
    protected final boolean isPlayer() {
        return (player != null);
    }
    
    private final boolean hasPermission() {
        return PermissionUtil.hasPermission(sender, permission);
    }
    
    protected final boolean argMatchesAlias(String arg, String... aliases) {
        for (String s : aliases) {
            if (arg.equalsIgnoreCase(s))
                return true;
        }
        return false;
    }
    
    protected final void error(String message, Object... args) {
        sendMessage(messages.getMessage(CommandMessage.ERROR), FormatUtil.format(message, args));
    }
    
    protected final void sendMessage(String message, Object... args) {
        sender.sendMessage(ChatColor.YELLOW + FormatUtil.format(message, args));
    }
    
    public final String getName() {
        return name;
    }
    
    public final String getDescription() {
        return description;
    }
    
    public final boolean hasPrefix() {
        return prefix != null && !prefix.isEmpty();
    }
    
    public final String getPrefix() {
        return prefix;
    }
    
    public final List<String> getAliases() {
        return aliases;
    }
    
    protected final GameDispenser getPlugin() {
        return plugin;
    }
    
    public String getUsageTemplate(final boolean displayHelp) {
        StringBuilder result = new StringBuilder();
        result.append("&b/");
        
        if (hasPrefix()) {
            result.append(prefix + " ");
        }
        
        result.append(name);
        
        for (String s : aliases) {
            result.append("," + s);
        }
        
        result.append("&3 ");
        for (String s : requiredArgs) {
            result.append(String.format("<%s> ", s));
        }
        
        for (String s : optionalArgs) {
            result.append(String.format("[%s] ", s));
        }
        
        if (displayHelp) {
            result.append("&e" + description);
        }
        
        return FormatUtil.format(result.toString());
    }
    
    protected class CommandMessage {
        protected static final String
            ERROR                        = "commands.error",
            INSUFFICIENT_PERMISSION      = "commands.insufficientPermission",
            TOO_FEW_ARGS                 = "commands.tooFewArgs",
            MUST_BE_PLAYER               = "commands.mustBePlayer",
            UNKNOWN_GAME                 = "commands.unknownGame",
            UNKNOWN_GAME_TYPE            = "commands.unknownGameType";
    }   
}