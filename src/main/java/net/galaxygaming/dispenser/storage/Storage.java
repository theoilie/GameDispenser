package net.galaxygaming.dispenser.storage;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import net.galaxygaming.dispenser.database.Database;

import org.bukkit.Bukkit;

public abstract class Storage {
	private static Database database = Database.getDatabase();
			
	public abstract String getName();
	
	public abstract String serialize();
	
	public abstract void deserialize(String storage);
	
	public static Storage deserialize(String storageString, Class<? extends Storage> clazz) {
		try {
			Storage storage = clazz.getConstructor().newInstance();
			storage.deserialize(storageString);
			return storage;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Storage item could not be deserialized: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	protected Database getDatabase() {
		return database;
	}
}