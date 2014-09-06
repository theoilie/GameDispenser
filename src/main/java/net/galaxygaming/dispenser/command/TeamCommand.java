package net.galaxygaming.dispenser.command;

import net.galaxygaming.dispenser.exception.TeamException;
import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.game.GameManager;
import net.galaxygaming.dispenser.game.TeamGame;
import net.galaxygaming.dispenser.team.Team;

import org.bukkit.permissions.Permission;

class TeamCommand extends Command {

    public TeamCommand() {
        this.prefix = "gd";
        this.name = "team";
        this.mustBePlayer = true;
        this.optionalArgs.add("team name");
        this.description = "Join a team.";
        this.permission = new Permission("gamedispenser.command.team");
    }
    
    @Override
    public void perform() {
        Game game = GameManager.getGameManager().getGameForPlayer(player);
        if (game == null) {
            error(messages.getMessage(CommandMessage.NOT_IN_GAME));
            return;
        }
        
        if (!(game instanceof TeamGame)) {
            error(messages.getMessage(CommandMessage.NOT_TEAM_GAME));
            return;
        }
        
        Team[] teams = ((TeamGame) game).getTeams().toArray(new Team[0]);
        
        if (args.length == 0) {
            printList(1, teams);
            return;
        }
        
        Team selectedTeam = null;
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(args[0])) {
                selectedTeam = team;
                break;
            }
        }
        
        if (selectedTeam == null) {
            error(messages.getMessage(CommandMessage.NO_TEAM), args[0]);
            printList(1, teams);
            return;
        }
        
        try {
            ((TeamGame) game).queueForTeam(player, selectedTeam);
        } catch (TeamException e) {
            // won't happen in this instance
        }
    }
}