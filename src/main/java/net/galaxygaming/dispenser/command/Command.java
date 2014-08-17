package net.galaxygaming.dispenser.command;

import java.util.List;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.MessagesResource;
import net.galaxygaming.util.FormatUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.google.common.collect.Lists;

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
            error(messages.getMessage(CommandMessage.NO_PERMISSION));
            return true;
        }
        
        perform();
        return true;
    }
    
    protected final boolean isPlayer() {
        return (player != null);
    }
    
    private final boolean hasPermission() {
        return sender.hasPermission(permission);
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
    
    protected final void error(String message, Throwable t) {
        error(message);
        if (t.getStackTrace() != null && t.getStackTrace().length != 0) {
            sendMessage(messages.getMessage(CommandMessage.SEE_CONSOLE));
            t.printStackTrace();
        }
    }
    
    protected final void sendMessage(String message, Object... args) {
        sender.sendMessage(ChatColor.YELLOW + FormatUtil.format(message, args));
    }
    
    protected final void printList(int page, Object[] objects) {
        int total = objects.length;
        int pages = total / 30;
        
        if (page < 1 || page > pages) {
            error(messages.getMessage(CommandMessage.NO_PAGE));
            return;
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = (page - 1) * 30; i < page * 30 && i < objects.length; i++) {
            result.append(objects[i].toString());
        }
        
        sendMessage(result.toString());
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
            NO_PERMISSION                = "commands.noPermission",
            TOO_FEW_ARGS                 = "commands.tooFewArgs",
            MUST_BE_PLAYER               = "commands.mustBePlayer",
            UNKNOWN_GAME                 = "commands.unknownGame",
            UNKNOWN_GAME_TYPE            = "commands.unknownGameType",
            ALREADY_IN_GAME              = "commands.alreadyInGame",
            SEE_CONSOLE                  = "commands.seeConsole",
            NOT_IN_GAME                  = "commands.notInGame",
            GAME_DELETED                 = "commands.gameDeleted",
            GAME_NOT_SETUP               = "commands.gameNotSetup",
            GAME_ALREADY_ACTIVE          = "commands.gameAlreadyActive",
            GAME_IS_FULL                 = "commands.gameIsFull",
            NO_SELECTION                 = "commands.noSelection",
            NO_COMPONENT                 = "commands.noComponent",
            NO_PAGE                      = "commands.noPage",
            NOT_A_NUMBER                 = "commands.notANumber",
            SET_COMPONENT_SUCCESS		= "commands.setComponentSuccess",
            GAME_CREATE_SUCCESS			= "commands.gameCreated",
            GAME_END_ATTEMPT				= "commands.attemptGameEnd",
            GAME_START_ATTEMPT			= "commands.attemptGameStart",
            GAME_LOADED					= "commands.gameLoaded",
            GAME_UNLOADED				= "commands.gameUnloaded",
        		WAND_MESSAGE					= "selection.wandMessage";
    }   
}