package net.galaxygaming.selection;

import java.util.Map;

import net.galaxygaming.util.LocationUtil;
import net.galaxygaming.util.SelectionUtil;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

public class Selection implements ConfigurationSerializable {
	private transient Player player;
	private Location pointOne, pointTwo;

	public Selection(Player player) {
		this.player = player;
		SelectionUtil.getInstance().addSelection(this);
	}
	
	public Selection(Player player, Location pointOne, Location pointTwo) {
		this.player = player;
		this.pointOne = pointOne;
		this.pointTwo = pointTwo;
		SelectionUtil.getInstance().addSelection(this);
	}
	
	/*
	 * This is for creating a Selection from 
	 * a config that does not belong to any 
	 * specific player, but rather to the server.
	 */
	public Selection(Location pointOne, Location pointTwo) {
		this.pointOne = pointOne;
		this.pointTwo = pointTwo;
	}
	
	public Location getPointOne() {
		return pointOne;
	}

	public void setPointOne(Location pointOne) {
		this.pointOne = pointOne;
	}

	public Location getPointTwo() {
		return pointTwo;
	}

	public void setPointTwo(Location pointTwo) {
		this.pointTwo = pointTwo;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public boolean arePointsSet() {
		return pointOne != null && pointTwo != null;
	}

	public boolean arePointsInSameWorld() {
		return pointOne.getWorld()
				.equals(pointTwo.getWorld());
	}
	
	public Map<Location, Block> getBlocks() {
		Map<Location, Block> blocks = Maps.newHashMap();
		
		World world = pointOne.getWorld();
		int x1 = pointOne.getBlockX(), y1 = pointOne.getBlockY(), z1 = pointOne.getBlockZ();
		int x2 = pointTwo.getBlockX(), y2 = pointTwo.getBlockY(), z2 = pointTwo.getBlockZ();
		
		int maxX = Math.max(x1, x2);
		int maxY = Math.max(y1, y2);
		int maxZ = Math.max(z1, z2);
		
		for (int minX = Math.min(x1, x2); minX < maxX; minX++) {
			for (int minY = Math.min(y1, y2); minY < maxY; minY++) {
				for (int minZ = Math.min(z1, z2); minZ < maxZ; minZ++) {
					Location loc = new Location(world, minX, minY, minZ);
					blocks.put(loc, world.getBlockAt(loc));
				}
			}
		}
		return blocks;
	}

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = Maps.newHashMap();
        result.put("min", LocationUtil.serializeLocation(pointOne));
        result.put("max", LocationUtil.serializeLocation(pointTwo));
        return result;
    }
    
    public static Selection deserialize(Map<String, Object> map) {
        Location pointOne = LocationUtil.deserializeLocation((String) map.get("min"));
        Location pointTwo = LocationUtil.deserializeLocation((String) map.get("max"));
        return new Selection(pointOne, pointTwo);
    }
}