package net.galaxygaming.dispenser.command;

import java.util.List;
import java.util.Set;

import net.galaxygaming.dispenser.game.GameType;

import org.bukkit.permissions.Permission;

import com.google.common.collect.Lists;

/**
 * 
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        this.prefix = "gd";
        this.name = "help";
        this.aliases.add("h");
        this.optionalArgs.add("game type");
        this.optionalArgs.add("page");
        this.description = "Shows a list of commands available";
        this.permission = new Permission("gamedispenser.command.help");
    }
    
    @Override
    public void perform() {
        int index = 1;
        GameType type = null;
        if (args.length > 0) {
            try {
                index = Integer.valueOf(args[args.length - 1]);
            } catch (NumberFormatException e) {
                if (args.length == 2) {
                    error(messages.getMessage(CommandMessage.NOT_A_NUMBER), args[1]);
                    return;
                }
            }

            type = GameType.get(args[0]);
            if (args.length == 2 && type == null) {
                error(messages.getMessage(CommandMessage.UNKNOWN_GAME_TYPE), args[0]);
                return;
            }
        }
        
        List<String> helpInfo = Lists.newArrayList();
        for (Command command : CommandManager.getCommandManager().root.subCommands) {
            if (sender.hasPermission(command.permission)) {
                helpInfo.add(command.getUsageTemplate(true));
            }
        }
        
        if (type != null) {
            Set<ReflectCommand> rCommands = CommandManager.getCommandManager().commands.get(type);
            for (ReflectCommand rCommand : rCommands) {
                if (rCommand instanceof PrefixedReflectCommand) {
                    for (Command command : ((PrefixedReflectCommand) rCommand).subCommands) {
                        if (sender.hasPermission(command.permission)) {
                            helpInfo.add(command.getUsageTemplate(true));
                        }                    }
                } else if (rCommand.executor instanceof Command) {
                    Command command = (Command) rCommand.executor;
                    if (sender.hasPermission(command.permission)) {
                        helpInfo.add(command.getUsageTemplate(true));
                    }
                }
            }
        }

        printList(index, "GameDispenser Help", helpInfo.toArray(new String[0]));
    }

}
