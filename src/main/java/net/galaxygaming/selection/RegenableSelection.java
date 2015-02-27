package net.galaxygaming.selection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.google.common.collect.Lists;
import net.galaxygaming.dispenser.game.Game;
import net.galaxygaming.dispenser.task.GameRunnable;

/**
 * Used for areas that should be restored
 * sometime during or after the game
 */
public class RegenableSelection {
    private int blocksPerInterval = 5000;
    private long intervalTicks = 10;
    
    private final Selection selection;
    private final Game game;
    private final String regionName;
    private byte[] blocks;
    private byte[] data;

    /**
     * @param game game this selection belongs to
     * @param regionName name of this region
     * @param selection selection specifying area
     */
    public RegenableSelection(Game game, String regionName, Selection selection) {
        this.selection = selection;
        this.game = game;
        this.regionName = regionName;
        
        reloadBlockData();
    }
    
    private RegenableSelection(Game game, String regionName, Selection selection, byte[] blocks, byte[] data) {
        this.selection = selection;
        this.game = game;
        this.regionName = regionName;
        
        this.blocks = blocks;
        this.data = data;
    }   
    
    /**
     * Updates the blocks saved by this selection with the blocks
     * currently in the world in the selection.
     */
    @SuppressWarnings("deprecation")
    public void reloadBlockData() {
        Location min = selection.getMin();
        Location max = selection.getMax();
        
        int Lx = max.getBlockX() - min.getBlockX() + 1;
        int Ly = max.getBlockY() - min.getBlockY() + 1;
        int Lz = max.getBlockZ() - min.getBlockZ() + 1;
        
        blocks = new byte[Lx*Ly*Lz];
        data = new byte[(int) Math.ceil((double) blocks.length / 2)];
        
        for (int i = 0; i < Lx; i++) {
            int x = i + min.getBlockX();
            for (int j = 0; j < Ly; j++) {
                int y = j + min.getBlockY();
                for (int k = 0; k < Lz; k++) {
                    int z = k + min.getBlockZ();
                    int index = i + j * Lx + k * Lx * Ly;
                    Block b = new Location(min.getWorld(), x, y, z).getBlock();
                    blocks[index] = (byte) b.getTypeId();
                    byte d = data[index / 2];                    
                    data[index / 2] = (byte) (d + (b.getData() << (4 * (index % 2))));
                }
            }
        }
        
        save();
    }
    
    /**
     * Gets the underlying {@link Selection} that defines the volume
     * controlled by this class
     * @return selection
     */
    public Selection getSelection() {
        return selection;
    }
    
    /**
     * Regenerates this selection with the stored block data.
     * This method uses block states in a update fast method
     * meaning no lighting updates are performed. It is also
     * processed over asynchronously over multiple seconds.
     */
    @SuppressWarnings("deprecation")
    public void regen() {
        // TODO: Check thread safety, cannot run at same time as reloadBlockData()
        
        new GameRunnable() {
            @Override
            public void run() {
                Location min = selection.getMin();
                Location max = selection.getMax();
                World world = min.getWorld();
                
                int Lx = max.getBlockX() - min.getBlockX() + 1;
                int Ly = max.getBlockY() - min.getBlockY() + 1;
                int Lz = max.getBlockZ() - min.getBlockZ() + 1;
                
                if (blocks == null || data == null || blocks.length != Lx*Ly*Lz || data.length != Lx*Ly*Lz/2) {
                    game.getLogger().log(Level.WARNING, "Tried to regen but block data is wrong length: " + regionName);
                    return;
                }
                
                List<BlockState> updates = Lists.newArrayList();
                
                for (int i = 0; i < Lx; i++) {
                    int x = i + min.getBlockX();
                    for (int j = 0; j < Ly; j++) {
                        int y = j + min.getBlockY();
                        for (int k = 0; k < Lz; k++) {
                            int z = k + min.getBlockZ();
                            int index = i + j * Lx + k * Lx * Ly;
                            BlockState state = world.getBlockAt(x, y, z).getState();
                            
                            int id = blocks[index] & 0xFF;
                            byte rawdata = (byte) (data[(int) Math.floor(index / 2)] >> (4 * (index % 2)));
                            if (state.getTypeId() == id && state.getRawData() == rawdata) {
                                continue;
                            }
                            
                            state.setTypeId(id);
                            state.setRawData(rawdata);
                            updates.add(state);
                        }
                    }
                }
                
                List<List<BlockState>> splitUpdates = Lists.newArrayList();
                
                for (int i = 0; i < Math.ceil(((double) updates.size()) / blocksPerInterval); i++) {
                    splitUpdates.add(Lists.<BlockState>newArrayList());
                }
                
                for (int i = 0; i < updates.size(); i++) {
                    splitUpdates.get((int) Math.floor(i / blocksPerInterval)).add(updates.get(i));
                }
                
                for (int i = 0; i < splitUpdates.size(); i++) {
                    final List<BlockState> states = splitUpdates.get(i);
                    new GameRunnable() {
                        @Override
                        public void run() {
                            for (BlockState state : states) {
                                state.update(true, false);
                            }
                        }
                    }.runTaskLater(i * intervalTicks);
                }
            }
        }.runTaskAsynchronously();
    }
    
    /**
     * Saves this regenable selection block data to disk
     */
    public void save() {
        if (!game.getType().getDataFolder().exists()) {
            game.getType().getDataFolder().mkdir();
        }
        
        File file = new File(game.getType().getDataFolder(), game.getName() + "_" + regionName);
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                game.getLogger().log(Level.WARNING, "Couldn't create file for region: " + regionName, e);
                return;
            }
        }
        
        FileOutputStream out = null;
        
        try {
            out = new FileOutputStream(file);
            ByteBuffer buf = ByteBuffer.allocate(8);
            buf.putInt(blocks.length);
            buf.putInt(data.length);
            out.write(buf.array());
            out.write(blocks);
            out.write(data);
            out.flush();
        } catch (FileNotFoundException e) {
            game.getLogger().log(Level.WARNING, "Could not find file: " + file.getName(), e);
        } catch (IOException e) {
            game.getLogger().log(Level.WARNING, "Error while saving to disk: " + regionName, e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    game.getLogger().log(Level.WARNING, "Error while closing file: " + file.getName(), e);
                }
            }
        }
        
        game.getConfig().set(regionName, selection);
        game.getConfig().set("blocks per interval", blocksPerInterval);
        game.getConfig().set("interval ticks", intervalTicks);
    }
    
    /** 
     * Loads a RegenableSelection for a game
     * @param game Game to load selection for
     * @param regionName name of region
     * @return RegenableSelection
     */
    public static RegenableSelection load(Game game, String regionName) {
        Selection selection = (Selection) game.getConfig().get(regionName);
        if (selection == null) {
            return null;
        }
        
        File file = new File(game.getType().getDataFolder(), game.getName() + "_" + regionName);
        
        if (!file.exists()) {
            game.getLogger().log(Level.WARNING, "Someone tried to load a region file that doesn't exist: " + regionName);
            return null;
        }
        
        FileInputStream in = null;
        byte[] blocks;
        byte[] data;
        
        try {
            in = new FileInputStream(file);
            byte[] head = new byte[8];
            in.read(head);
            ByteBuffer wrapped = ByteBuffer.wrap(head);
            blocks = new byte[wrapped.getInt()];
            data = new byte[wrapped.getInt()];
            in.read(blocks);
            in.read(data);
            
            RegenableSelection region = new RegenableSelection(game, regionName, selection, blocks, data);
            region.blocksPerInterval = game.getConfig().getInt("blocks per interval", region.blocksPerInterval);
            region.intervalTicks = game.getConfig().getLong("interval ticks", region.intervalTicks);
            return region;
        } catch (FileNotFoundException e) {
            game.getLogger().log(Level.WARNING, "Could not find file: " + file.getName(), e);
        } catch (IOException e) {
            game.getLogger().log(Level.WARNING, "Error while saving to disk: " + regionName, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    game.getLogger().log(Level.WARNING, "Error while closing file: " + file.getName(), e);
                }
            }
        }
        return null;
    }
}
