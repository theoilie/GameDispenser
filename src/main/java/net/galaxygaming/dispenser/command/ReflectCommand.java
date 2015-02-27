package net.galaxygaming.dispenser.command;


import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

class ReflectCommand extends org.bukkit.command.Command {

    private CommandExecutor executor;
    
    ReflectCommand(String name, Command command, CommandExecutor executor) {
        super(name, command.getDescription(), command.getUsageTemplate(false), command.getAliases());        
        setExecutor(executor);
    }
    
    ReflectCommand(String name) {
        super(name);
    }

    ReflectCommand(Command command, CommandExecutor executor) {
        this(command.getName(), command, executor);
    }
    
    void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }
    
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (executor != null) {
            return executor.onCommand(sender, this, commandLabel, args);
        }
        return false;
    }   
}