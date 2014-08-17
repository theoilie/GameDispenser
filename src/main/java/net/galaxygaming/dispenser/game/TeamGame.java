package net.galaxygaming.dispenser.game;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.galaxygaming.dispenser.team.Team;
import net.galaxygaming.util.FormatUtil;

/**
 * 
 */
public abstract class TeamGame extends GameBase {

    protected List<Team> teams = Lists.newArrayList();
    private final Map<Player, Team> queue = Maps.newLinkedHashMap();
    
    /**
     * Gives an immutable list of teams in this game 
     * @return list of teams
     */
    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }
    
    /**
     * Adds a team to the game's team list
     * @param team
     */
    public void addTeam(Team team) {
        teams.add(team);
    }
    
    /**
     * Removes a team from the team listing.
     * Also removes any players from the
     * team object's player list.
     * @param team
     */
    public void removeTeam(Team team) {
        teams.remove(team);
        team.reset();
    }
    
    /**
     * Gives the number of teams
     * @return team count
     */
    public int getTeamCount() {
        return teams.size();
    }
    
    @Override
    public void onStart() {
        Set<Player> players = Sets.newHashSet(getPlayers());
        int teamPlayerCount = players.size() / getTeamCount();
        for (Entry<Player, Team> entry : queue.entrySet()) {
            // Add queued players in the order they queued but also maintain team balance
            if (entry.getValue().getSize() < teamPlayerCount) {
                entry.getValue().add(entry.getKey());
                players.remove(entry.getKey());
            }
        }
        
        Random r = new Random();
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Team team = null;
            while (team == null) {
                team = teams.get(r.nextInt(getTeamCount()));
                if (team.getSize() > teamPlayerCount) {
                    team = null;
                }
            }
            Player player = it.next();
            team.add(player);
            it.remove();
        }
        queue.clear();
    }
    
    @Override
    public void onEnd() {
        for (Team team : teams) {
            team.reset();
        }
    }
    
    /**
     * Queues a player to join a team when 
     * the game starts
     * @param player
     * @param team
     */
    public void queueForTeam(Player player, Team team) {
        if (!teams.contains(team)) {
            throw new RuntimeException("Cannot queue for team as it is not in this game");
        }
        
        if (queue.containsKey(player)) {
            queue.remove(player);
        }
        
        queue.put(player, team);
        
        int count = 0;
        for (Team t : queue.values()) {
            if (t.equals(team)) {
                count++;
            }
        }
        
        player.sendMessage(FormatUtil.format("&eYou are place &3{0}&e in the queue for team &3{1}", count, team.getName()));
    }
    
}