package net.galaxygaming.selection;

import java.util.Map;

import net.galaxygaming.util.LocationUtil;
import net.galaxygaming.util.SelectionUtil;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

/**
 * Selections are made to define areas.
 * For restoring selections see
 * {@link net.galaxygaming.selection.RegenableSelection}
 * class.
 */
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
	
	/**
	 * Clones a selection so it's unmodifiable
	 * @param selection
	 */
	public Selection(Selection selection) {
	    this(selection.pointOne, selection.pointTwo);
	}
	
	/**
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
	
	public Block[] getBlocks() {
        Location min = getMin();
        Location max = getMax();
        
        int Lx = max.getBlockX() - min.getBlockX() + 1;
        int Ly = max.getBlockY() - min.getBlockY() + 1;
        int Lz = max.getBlockZ() - min.getBlockZ() + 1;
        
        Block[] blocks = new Block[Lx*Ly*Lz];

	    for (int i = 0; i < Lx; i++) {
	        int x = i + min.getBlockX();
	        for (int j = 0; j < Ly; j++) {
	            int y = j + min.getBlockY();
	            for (int k = 0; k < Lz; k++) {
	                int z = k + min.getBlockZ();
	                blocks[i + j * Lx + k * Lx * Ly] = min.getWorld().getBlockAt(x, y, z);
	            }
	        }
	    }
		return blocks;
	}
	
	Location getMin() {
	    return new Location(pointOne.getWorld(), 
                Math.min(pointOne.getBlockX(), pointTwo.getBlockX()), 
                Math.min(pointOne.getBlockY(), pointTwo.getBlockY()),
                Math.min(pointOne.getBlockZ(), pointTwo.getBlockZ()));
	}
	
	Location getMax() {
        return new Location(pointOne.getWorld(), 
                Math.max(pointOne.getBlockX(), pointTwo.getBlockX()), 
                Math.max(pointOne.getBlockY(), pointTwo.getBlockY()),
                Math.max(pointOne.getBlockZ(), pointTwo.getBlockZ()));
	}

	public boolean isIn(Location loc) {
	    return loc.toVector().isInAABB(getMin().toVector(), getMax().toVector());
	}
	
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = Maps.newHashMap();
        result.put("min", LocationUtil.serializeLocation(getMin()));
        result.put("max", LocationUtil.serializeLocation(getMax()));
        return result;
    }
    
    @Override
    public Selection clone() {
        return new Selection(this);
    }
    
    public static Selection deserialize(Map<String, Object> map) {
        Location pointOne = LocationUtil.deserializeLocation((String) map.get("min"));
        Location pointTwo = LocationUtil.deserializeLocation((String) map.get("max"));
        return new Selection(pointOne, pointTwo);
    }
}