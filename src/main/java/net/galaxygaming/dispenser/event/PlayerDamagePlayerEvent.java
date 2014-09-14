package net.galaxygaming.dispenser.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamagePlayerEvent extends EntityDamageEvent implements Cancellable {
	private Player damager;
	private Player damagee;
	private double damage;
	private boolean cancel;
	private boolean isPlayer1InGame, isPlayer2InGame;

	/**
	 * When a player hurts another player
	 * @param damager the player who damaged the entity
	 * @param damagee the entity who was hurt
	 * @param damage the amount of damage done (2 damage = 1 heart)
	 */
	public PlayerDamagePlayerEvent(Player damager, Player damagee, double damage) {
		super(damagee, DamageCause.ENTITY_ATTACK, damage);
		this.damager = damager;
		this.damagee = damagee;
		this.damage = damage;
	}

	/**
	 * Gets the damager
	 * @return the player who dealt the damage
	 */
	public Player getDamager() {
		return damager;
	}
	
	/**
	 * Gets the damagee
	 * @return the player who was hurt by the damager
	 */
	public Player getDamagee() {
		return damagee;
	}
	
	/**
	 * Gets the amount of damage done
	 * @return the amount of damage dealt
	 */
	@Override
	public double getDamage() {
		return damage;
	}
	
	/**
	 * Sets the amount of damage done
	 * @param damage the amount of damager has caused the damagee
	 */
	public void setDamage(double damage) {
		this.damage = damage;
	}
	
	/**
	 * Checks if the damager is in a game
	 * @return true if the damager is playing a minigame
	 */
	public boolean isPlayer1InGame() {
		return isPlayer1InGame;
	}

	/**
	 * Sets if the damager is in a game
	 * @param isPlayer1InGame whether or not the damager is playing a minigame
	 */
	public void setPlayer1InGame(boolean isPlayer1InGame) {
		this.isPlayer1InGame = isPlayer1InGame;
	}
	
	/**
	 * Checks if the damagee is in a game
	 * @return true if the damagee is playing a minigame
	 */
	public boolean isPlayer2InGame() {
		return isPlayer2InGame;
	}

	/**
	 * Sets if the damagee is in a game
	 * @param isPlayer1InGame whether or not the damaged is playing a minigame
	 */
	public void setPlayer2InGame(boolean isPlayer2InGame) {
		this.isPlayer2InGame = isPlayer2InGame;
	}
	
	@Override
	public HandlerList getHandlers() {
		return super.getHandlers();
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}