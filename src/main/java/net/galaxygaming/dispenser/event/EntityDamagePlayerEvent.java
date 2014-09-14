package net.galaxygaming.dispenser.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamagePlayerEvent extends EntityDamageEvent implements Cancellable {
	private Entity damager;
	private Player damagee;
	private double damage;
	private boolean cancel;

	/**
	 * When an entity hurts a player
	 * @param damager the entity that damaged the player
	 * @param damagee the player who was hurt
	 * @param damage the amount of damage done (2 damage = 1 heart)
	 */
	public EntityDamagePlayerEvent(Entity damager, Player damagee, double damage) {
		super(damagee, DamageCause.ENTITY_ATTACK, damage);
		this.damager = damager;
		this.damagee = damagee;
		this.damage = damage;
	}

	/**
	 * Gets the damager
	 * @return the entity who dealt the damage
	 */
	public Entity getDamager() {
		return damager;
	}
	
	/**
	 * Gets the damagee
	 * @return the player who was hurt by the entity
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
	 * @param damage the amount of damage the entity has caused the player
	 */
	public void setDamage(double damage) {
		this.damage = damage;
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