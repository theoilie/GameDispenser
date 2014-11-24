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
 * {@link net.galaxygaming.selection.RegenableSelection}.
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
	 * @param selection the selection to clone
	 */
	public Selection(Selection selection) {
	    this(selection.pointOne, selection.pointTwo);
	}
	
	/**
	 * This is for creating a Selection from 
	 * a config that does not belong to any 
	 * specific player, but rather to the server.
	 * @param pointOne the first point
	 * @param pointTwo the second point
	 */
	public Selection(Location pointOne, Location pointTwo) {
		this.pointOne = pointOne;
		this.pointTwo = pointTwo;
	}
	
	/**
	 * Gets the first corner in the selection
	 * @return the first point
	 */
	public Location getPointOne() {
		return pointOne;
	}

	/**
	 * Sets the first corner in the selection
	 * @param pointOne the first point
	 */
	public void setPointOne(Location pointOne) {
		this.pointOne = pointOne;
	}

	/**
	 * Gets the second corner in the selection
	 * @return the second point
	 */
	public Location getPointTwo() {
		return pointTwo;
	}

	/**
	 * Sets the second corner in the selection
	 * @param pointTwo the second point
	 */
	public void setPointTwo(Location pointTwo) {
		this.pointTwo = pointTwo;
	}

	/**
	 * Gets the player associated with this selection
	 * @return the owner of this selection
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Sets the player associated with this selection
	 * @param player the owner of this selection
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/**
	 * Checks if both points are set
	 * @return true if both points are set
	 */
	public boolean arePointsSet() {
		return pointOne != null && pointTwo != null;
	}

	/**
	 * Checks if points are in the same world
	 * @return true if points are in the same world
	 */
	public boolean arePointsInSameWorld() {
		return pointOne.getWorld()
				.equals(pointTwo.getWorld());
	}
	
	/**
	 * Gets the blocks within point one and point two
	 * @return the blocks inside the selection
	 */
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
	
	/**
	 * Gets the lowest boundary
	 * @return the lowest point
	 */
	Location getMin() {
	    return new Location(pointOne.getWorld(), 
                Math.min(pointOne.getBlockX(), pointTwo.getBlockX()), 
                Math.min(pointOne.getBlockY(), pointTwo.getBlockY()),
                Math.min(pointOne.getBlockZ(), pointTwo.getBlockZ()));
	}
	
	/**
	 * Gets the highest boundary
	 * @return the highest point
	 */
	Location getMax() {
        return new Location(pointOne.getWorld(), 
                Math.max(pointOne.getBlockX(), pointTwo.getBlockX()), 
                Math.max(pointOne.getBlockY(), pointTwo.getBlockY()),
                Math.max(pointOne.getBlockZ(), pointTwo.getBlockZ()));
	}

	/**
	 * Checks if a location is inside this selection
	 * @param loc the location to be checked
	 * @return true if loc is inside this selection
	 */
	public boolean isIn(Location loc) {
	    return loc.toVector().isInAABB(getMin().toVector(), getMax().toVector());
	}
	
	/**
	 * Serializes the selection so that it can be saved in a config
	 * @return a serialized form of this selection
	 */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = Maps.newHashMap();
        result.put("min", LocationUtil.serializeLocation(getMin()));
        result.put("max", LocationUtil.serializeLocation(getMax()));
        return result;
    }
    
    @Override
    public String toString() {
        return "min:" + LocationUtil.serializeLocationShort(getMin()) + ", max:" + LocationUtil.serializeLocationShort(getMax());
    }
    
    /**
     * Clones this selection
     * @return a clone of this selection
     */
    @Override
    public Selection clone() {
        return new Selection(this);
    }
    
    /**
     * Turns a serialized selection into an Object
     * @param map the serialized form of the selection
     * @return a Selection object from the map
     */
    public static Selection deserialize(Map<String, Object> map) {
        Location pointOne = LocationUtil.deserializeLocation((String) map.get("min"));
        Location pointTwo = LocationUtil.deserializeLocation((String) map.get("max"));
        return new Selection(pointOne, pointTwo);
    }
}