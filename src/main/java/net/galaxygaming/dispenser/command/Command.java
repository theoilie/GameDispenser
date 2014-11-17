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
    
    /** The sender of this command */
    protected CommandSender sender;
    /** The player that sent this command if it was a player */
    protected Player player;
    /** Arguments the sender sent with the command */
    protected String args[];
    
    /** The name of this command */
    protected String name = "";
    /** The description of this command as shown to the user when checking help */
    protected String description = "";
    /** The permission required to use this command */
    protected Permission permission = null;
    
    /** Whether the sender must be a player */
    protected boolean mustBePlayer;
    /** A list of required arguments */
    protected List<String> requiredArgs = Lists.newArrayList();
    /** A list of optional arguments */
    protected List<String> optionalArgs = Lists.newArrayList();
    /** A list of aliases this command can be performed as */
    protected List<String> aliases = Lists.newArrayList();
    
    /** A prefix for this command if desired */
    protected String prefix = "";
    
    /** 
     * A {@link MessagesResource} that looks for a messages properties file in 
     * the game linked to this command's jar file but will fall back to 
     * GameDispenser's default messages file.
     */
    protected MessagesResource messages = new MessagesResource(getClass().getClassLoader());
    
    /**
     * Called whenever a command passes the following checks:
     * <ul>
     * <li>if {@link #mustBePlayer} is true then sender must be an instance of {@link Player}</li>
     * <li>all required args are entered</li>
     * <li>sender has the necessary permission required</li>
     * </ul> 
     */
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
    
    /**
     * @return true if the sender of this command is a player
     */
    protected final boolean isPlayer() {
        return (player != null);
    }
    
    private final boolean hasPermission() {
        return sender.hasPermission(permission);
    }
    
    /**
     * Checks if a string matches any of the aliases provided
     * @param arg argument to check
     * @param aliases aliases
     * @return true if match found
     */
    protected final boolean argMatchesAlias(String arg, String... aliases) {
        for (String s : aliases) {
            if (arg.equalsIgnoreCase(s))
                return true;
        }
        return false;
    }
    
    /**
     * Formats and then sends an error message to the sender
     * @param message format of the message
     * @param args any formatting parameters
     */
    protected final void error(String message, Object... args) {
        sendMessage(messages.getMessage(CommandMessage.ERROR), FormatUtil.format(message, args));
    }
    
    /**
     * Sends an error message about this exception and prints stack trace
     * @param message contents of message
     * @param t throwable
     */
    protected final void error(String message, Throwable t) {
        error(message);
        error(t);
    }
    
    protected final void error(Throwable t) {
        if (t.getStackTrace() != null && t.getStackTrace().length != 0) {
            sendMessage(messages.getMessage(CommandMessage.SEE_CONSOLE));
            t.printStackTrace();
        }
    }
    
    /**
     * Formats and then sends a message to the sender
     * @param message format of the message
     * @param args any formatting parameters
     */
    protected final void sendMessage(String message, Object... args) {
        sender.sendMessage(ChatColor.YELLOW + FormatUtil.format(message, args));
    }
    
    /**
     * Prints an array of objects, 9 a page
     * @param page page to print
     * @param objects array of objects
     */
    protected final void printList(int page, String header, Object[] objects) {
        int total = objects.length;
        int pages = (total + 8) / 9;
        
        if (page < 1 || page > pages) {
            error(messages.getMessage(CommandMessage.NO_PAGE), page);
            return;
        }
        
        sendMessage(messages.getMessage(CommandMessage.LIST_HEADER_FORMAT), header, page, pages);
        for (int i = (page - 1) * 9; i < page * 9 && i < objects.length; i++) {
            sendMessage(objects[i].toString());
        }
    }
    
    /**
     * Gives the name of this command
     * @return name
     */
    public final String getName() {
        return name;
    }
    
    /**
     * Gives the description for this command
     * @return description
     */
    public final String getDescription() {
        return description;
    }
    
    /**
     * Returns true if this command has a prefix, otherwise false
     * @return true if prefixed
     */
    public final boolean hasPrefix() {
        return prefix != null && !prefix.isEmpty();
    }
    
    /**
     * Gives the prefix for this command
     * @return prefix
     */
    public final String getPrefix() {
        return prefix;
    }
    
    /**
     * Gives a list of aliases associated with this command
     * @return aliases
     */
    public final List<String> getAliases() {
        return aliases;
    }
    
    /**
     * A reference to GameDispenser
     * @return GameDispenser main instance
     */
    protected final GameDispenser getPlugin() {
        return plugin;
    }
    
    /**
     * Gives a string representing how this command can be used
     * @param displayHelp
     * @return usage string
     */
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
    
    /**
     * Predefined messages in GameDispenser's {@link MessagesResource}.
     */
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
            NO_PAGE                      = "commands.noPage",
            NOT_A_NUMBER                 = "commands.notANumber",
            SET_COMPONENT_SUCCESS		= "component.successful",
            GAME_CREATE_SUCCESS			= "commands.gameCreated",
            GAME_END_ATTEMPT				= "commands.attemptGameEnd",
            GAME_START_ATTEMPT			= "commands.attemptGameStart",
            GAME_LOADED					= "commands.gameLoaded",
            GAME_UNLOADED				= "commands.gameUnloaded",
            WAND_MESSAGE					= "selection.wandMessage",
            NO_TEAM                      = "commands.noTeam",
            NOT_TEAM_GAME                = "commands.notTeamGame",
            NO_KIT						= "commands.noKit",
            LIST_HEADER_FORMAT           = "commands.listHeaderFormat";
    }   
}