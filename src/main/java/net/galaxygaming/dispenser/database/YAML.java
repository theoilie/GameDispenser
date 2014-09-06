package net.galaxygaming.dispenser.database;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import net.galaxygaming.dispenser.GameDispenser;
import net.galaxygaming.dispenser.game.GameManager;

public class YAML extends Database {

    private static final String FOLDER_NAME = "database";
    private static final String FILE_EXTENSION = "";
    
    private final File folder;
    private final Thread mainThread;
    
    private final ConcurrentMap<String, ConcurrentMap<String, Object>> data;
    private final Queue<Runnable> tasks;
    
    public YAML() {
        this.folder = new File(GameDispenser.getInstance().getDataFolder(), FOLDER_NAME);
        this.mainThread = Thread.currentThread();
        
        if (!folder.exists()) {
            folder.mkdir();
        }
        
        this.data = new ConcurrentHashMap<String, ConcurrentMap<String, Object>>(64, 0.75f, 64);
        this.tasks = new ConcurrentLinkedQueue<Runnable>();
        
        new BukkitRunnable() {
            public void run() {
                processQueue();
            }
        }.runTaskTimerAsynchronously(GameDispenser.getInstance(), 2L, 2L);
        
        /* Saves and cleans data every 10 minutes TODO: configurable */
        scheduleTask(new Runnable() {
            public void run() {
                cleanData();
            }
        }, 20L * 60 * 10);
        scheduleTask(new Runnable() {
            public void run() {
                save();
            }
        }, 20L * 60 * 10);
    }
    
    private void scheduleTask(final Runnable runnable, final long period) {
        new BukkitRunnable() {
            public void run() {
                tasks.offer(runnable);
            }
        }.runTaskTimerAsynchronously(GameDispenser.getInstance(), 20L, period);
    }
    
    private void ensureDataIsLoaded(final String key) {
        Validate.isTrue(Thread.currentThread() != mainThread, "Cannot call database methods from main thread.");
        if (!data.containsKey(key)) {
            Runnable loadTask = new Runnable() {
                public void run() {
                    loadData(key);
                }
            };
            tasks.offer(loadTask);
            while (tasks.contains(loadTask)) { // process queue doesn't remove task til after its completed
                try {
                    wait(150L);
                } catch (InterruptedException e) {
                }
            }
        } 
    }
    
    private Object getData(final String key, final String[] subkeys) {
        ensureDataIsLoaded(key);
        return getValue(data, key, subkeys);
    }
    
    private void setData(final String key, final String[] subkeys, Object value) {
        ensureDataIsLoaded(key);
        setValue(data.get(key), subkeys[0], subkeys.length <= 1 ? null : Arrays.copyOfRange(subkeys, 1, subkeys.length), value);
    }
    
    /* Recursively go through maps til find right value */
    @SuppressWarnings("unchecked")
    private Object getValue(Map<String, ?> map, String key, String[] subkeys) {
        Object val = map.get(key);
        if (subkeys == null || subkeys.length == 0) {
            return val;
        } else {
            if (val == null) {
                val = new ConcurrentHashMap<String, Object>(16, 0.75f, 8);
                return null;
            }
            
            Validate.isTrue(val instanceof Map);
            String[] subs = subkeys.length == 1 ? null : Arrays.copyOfRange(subkeys, 1, subkeys.length);
            return getValue((Map<String,Object>) val, subkeys[0], subs);
        }
    }
    
    /* Recursively go through maps to set right value */
    @SuppressWarnings("unchecked")
    private void setValue(Map<String, Object> map, String key, String[] subkeys, Object value) {
        if (subkeys == null || subkeys.length == 0) {
            map.put(key, value);
        } else {
            Object val = map.get(key);
            if (val == null) {
                val = new ConcurrentHashMap<String, Object>(16, 0.75f, 8);
                map.put(key, val);
            }
            
            Validate.isTrue(val instanceof Map);
            String[] subs = subkeys.length == 1 ? null : Arrays.copyOfRange(subkeys, 1, subkeys.length);
            setValue((Map<String, Object>) val, subkeys[0], subs, value);
        }
    }
    
    private void loadData(final String key) {
        ConfigurationSection config = YamlConfiguration.loadConfiguration(new File(folder, getFileName(key)));
        data.put(key, getConcurrentMap(config.getValues(true)));
    }
    
    /* Recursively adds concurrent maps to arbitrary depth of key, value pairs */
    @SuppressWarnings("unchecked")
    private ConcurrentMap<String, Object> getConcurrentMap(Map<String, Object> map) {
        ConcurrentMap<String, Object> concurrentMap = new ConcurrentHashMap<String, Object>(16, 0.75f, 8);
        Iterator<Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            if (entry.getValue() instanceof Map) {
                concurrentMap.put(entry.getKey(), getConcurrentMap((Map<String, Object>) entry.getValue()));
            } else {
                concurrentMap.put(entry.getKey(), entry.getValue());
            }
        }
        return concurrentMap;
    }
    
    private void save() {
        Iterator<Entry<String, ConcurrentMap<String, Object>>> it = data.entrySet().iterator();
        while(it.hasNext()) {
            Entry<String, ConcurrentMap<String, Object>> entry = it.next();
            save(entry.getKey(), entry.getValue());
        }
    }
    
    private void save(String key, Map<String, Object> value) {
        File file = new File(folder, getFileName(key));
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Iterator<Entry<String, Object>> it = value.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            config.set(entry.getKey(), entry.getValue());
        }
        
        try {
            config.save(file);
        } catch (IOException e) { 
            // TODO: something
        }
    }
    
    private void removeData(final String key) {
        Map<String, Object> value = data.remove(key);
        save(key, value);
    }
    
    private void cleanData() {
        for (String key : data.keySet()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(key));
            if ((GameManager.getGameManager().getGame(key) == null
                    && player == null) || !player.isOnline()) {
                removeData(key);
            }
        }
    }
    
    private String getFileName(final String name) {
        return name + FILE_EXTENSION;
    }
    
    private void processQueue() {
        Iterator<Runnable> it = tasks.iterator();
        while (it.hasNext()) {
            Runnable task = it.next();
            task.run();
            it.remove();
        }
    }
    
	@Override
	protected Object get(String minigame, UUID playerUUID, String key) {
		return get(minigame, playerUUID, key, null, null);
	}

	@Override
	protected Object get(String minigame, UUID playerUUID, String key, String keyType, Object defaultValue) {
	    Object result = getData(minigame, new String[] { playerUUID.toString(), key });
	    return result == null ? defaultValue : result;
	}
	
	@Override
	protected void set(String minigame, UUID playerUUID, String playerName, String key, Object value) {
	    set(minigame, playerUUID, playerName, key, null, value);
	}
	
	@Override
	protected void set(String minigame, UUID playerUUID, String playerName, String key, String valueType, Object value) {
		setData(minigame, new String[] { playerUUID.toString(), key }, value);
	}

	@Override
	protected Object get(UUID playerUUID, String key) {
		return get(playerUUID, key, null, null);
	}
	
	@Override
	protected Object get(UUID playerUUID, String key, String keyType, Object defaultValue) {
	    Object result = getData(playerUUID.toString(), new String[] { key });
        return result == null ? defaultValue : result;	
	}

	@Override
	protected void set(UUID playerUUID, String playerName, String key, Object value) {
	    set(playerUUID, playerName, key, null, value);
	}
	
	@Override
	protected void set(UUID playerUUID, String playerName, String key, String valueType, Object value) {
	    setData(playerUUID.toString(), new String[] { key }, value);
	    setData(playerUUID.toString(), new String[] { "name" }, playerName); // TODO: is this what you want to do with player name???
	}

	@Override
	public void unload() {
	    tasks.clear();
		save();
	}
}