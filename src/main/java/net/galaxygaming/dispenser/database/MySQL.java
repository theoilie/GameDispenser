package net.galaxygaming.dispenser.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

import net.galaxygaming.dispenser.GameDispenser;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class MySQL extends Database {
	private static Connection connection;
	private GameDispenser main = GameDispenser.getInstance();
	private static final String MISC_TABLE = GameDispenser.getInstance().getConfig().getString("mysql.miscellaneous table");
	private static final String ORDERED_TABLE = GameDispenser.getInstance().getConfig().getString("mysql.ordered table");

	@Override
	protected Object get(String minigame, UUID playerUUID, String key) {
		ResultSet rs = null;
		Statement statement = null;
		Object result = null;
		try {
			if (connection == null)
				openConnection();
			ensurePlayer(Bukkit.getOfflinePlayer(playerUUID), minigame, key, "varchar (200)", "0");
			statement = connection.createStatement();
			if (statement.execute("SELECT * FROM " + minigame)) {
				rs = statement.getResultSet();
				while (rs.next()) {
					UUID uuid = (UUID) rs.getObject(1);
					if (uuid != playerUUID)
						continue;
					result = rs.getObject(key);
				}
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}
	
	@Override
	protected Object get(String minigame, UUID playerUUID, String key, String defaultType, Object defaultValue) {
		ResultSet rs = null;
		Statement statement = null;
		Object result = null;
		try {
			if (connection == null)
				openConnection();
			ensurePlayer(Bukkit.getOfflinePlayer(playerUUID), minigame, key, defaultType, defaultValue);
			statement = connection.createStatement();
			if (statement.execute("SELECT * FROM " + minigame)) {
				rs = statement.getResultSet();
				while (rs.next()) {
					UUID uuid = (UUID) rs.getObject(1);
					if (uuid != playerUUID)
						continue;
					result = rs.getObject(key);
				}
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	protected void set(String minigame, UUID playerUUID, String playerName, String key, Object value) {
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		try {
			if (connection == null)
				openConnection();
			ensurePlayer(Bukkit.getOfflinePlayer(playerUUID), minigame, key, "varchar (200)", value);
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + minigame + " (PlayerUUID varchar(200), PlayerName varchar(200))");
			preparedStatement = connection.prepareStatement("UPDATE " + minigame + " SET " + key + " = ? WHERE PlayerUUID = ?");
			preparedStatement.setObject(1, value);
			preparedStatement.setObject(2, playerUUID);
			preparedStatement.execute();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void set(String minigame, UUID playerUUID, String playerName, String key, String valueType, Object value) {
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		try {
			if (connection == null)
				openConnection();
			ensurePlayer(Bukkit.getOfflinePlayer(playerUUID), minigame, key, valueType, value);
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + minigame + " (PlayerUUID varchar(200), PlayerName varchar(200))");
			preparedStatement = connection.prepareStatement("UPDATE " + minigame + " SET " + key + " = ? WHERE PlayerUUID = ?");
			preparedStatement.setObject(1, value);
			preparedStatement.setObject(2, playerUUID);
			preparedStatement.execute();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected Object get(UUID playerUUID, String key) {
		ResultSet rs = null;
		Statement statement = null;
		Object result = null;
		try {
			if (connection == null)
				openConnection();
			ensurePlayer(Bukkit.getOfflinePlayer(playerUUID), MISC_TABLE, key, "varchar (200)", "0");
			statement = connection.createStatement();
			if (statement.execute("SELECT * FROM " + MISC_TABLE)) {
				rs = statement.getResultSet();
				while (rs.next()) {
					UUID uuid = (UUID) rs.getObject(1);
					if (uuid != playerUUID)
						continue;
					result = rs.getObject(key);
				}
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	protected Object get(UUID playerUUID, String key, String defaultType, Object defaultValue) {
		ResultSet rs = null;
		Statement statement = null;
		Object result = null;
		try {
			if (connection == null)
				openConnection();
			ensurePlayer(Bukkit.getOfflinePlayer(playerUUID), MISC_TABLE, key, defaultType, defaultValue);
			statement = connection.createStatement();
			if (statement.execute("SELECT * FROM " + MISC_TABLE)) {
				rs = statement.getResultSet();
				while (rs.next()) {
					UUID uuid = (UUID) rs.getObject(1);
					if (uuid != playerUUID)
						continue;
					result = rs.getObject(key);
				}
			}
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}
	
	@Override
	protected void set(UUID playerUUID, String playerName, String key, Object value) {
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		try {
			if (connection == null)
				openConnection();
			ensurePlayer(Bukkit.getOfflinePlayer(playerUUID), MISC_TABLE, key, "varchar (200)", value);
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + MISC_TABLE + " (PlayerUUID varchar(200), PlayerName varchar(200))");
			preparedStatement = connection.prepareStatement("UPDATE " + MISC_TABLE + " SET " + key + " = ? WHERE PlayerUUID = ?");
			preparedStatement.setObject(1, value);
			preparedStatement.setObject(2, playerUUID);
			preparedStatement.execute();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void set(UUID playerUUID, String playerName, String key, String valueType, Object value) {
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		try {
			if (connection == null)
				openConnection();
			ensurePlayer(Bukkit.getOfflinePlayer(playerUUID), MISC_TABLE, key, valueType, value);
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + MISC_TABLE + " (PlayerUUID varchar(200), PlayerName varchar(200))");
			preparedStatement = connection.prepareStatement("UPDATE " + MISC_TABLE + " SET " + key + " = ? WHERE PlayerUUID = ?");
			preparedStatement.setObject(1, value);
			preparedStatement.setObject(2, playerUUID);
			preparedStatement.execute();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void unload() {
		if (connection == null)
			return;
		try {
			connection.close();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with closing SQL connection: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String host = main.getConfig().getString("mysql.host");
			String port = main.getConfig().getString("mysql.port");
			String database = main.getConfig().getString("mysql.database");
			String user = main.getConfig().getString("mysql.user");
			String pass = main.getConfig().getString("mysql.pass");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, pass);
		} catch (ClassNotFoundException | SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with opening SQL connection: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void ensurePlayer(OfflinePlayer player, String table, String key, String valueType, Object defaultValue) {
		UUID uuid = player.getUniqueId();
		String name = player.getName();
		Statement statement = null;
		ResultSet rs = null;
		try {
			if (connection == null)
				openConnection();
			statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (PlayerUUID varchar(200), PlayerName varchar(200), " + key + " " + valueType + ")");
			rs = statement.executeQuery("SELECT * FROM " + table + " WHERE PlayerUUID = " + uuid + " LIMIT 1");
			if (!rs.next())
				statement.execute("INSERT INTO " + table + " (PlayerUUID,  PlayerName, " + key + ") VALUES ("+ uuid + ", " + name + ", " + defaultValue + ")");
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with opening SQL connection: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "Error with opening SQL connection: " + e.getMessage());
				e.printStackTrace();
			}
		}
		updateName(uuid, name, table);
	}
	
	private void updateName(UUID uuid, String name, String table) {
		PreparedStatement preparedStatement = null;
		try {
			if (connection == null)
				openConnection();
			preparedStatement = connection.prepareStatement("UPDATE " + table + " SET PlayerName = ? WHERE PlayerUUID = ?");
			preparedStatement.setObject(1, name);
			preparedStatement.setObject(2, uuid);
			preparedStatement.execute();
		} catch (SQLException e) {
			main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException e) {
				main.getLogger().log(Level.SEVERE, "Error with MySQL: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}