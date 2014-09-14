package net.galaxygaming.dispenser.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageEntityEvent extends EntityDamageEvent implements Cancellable {
	private Player damager;
	private LivingEntity damagee;
	private double damage;
	private boolean cancel;

	/**
	 * When a player hurts an entity
	 * @param damager the player who damaged the entity
	 * @param damagee the entity who was hurt
	 * @param damage the amount of damage done (2 damage = 1 heart)
	 */
	public PlayerDamageEntityEvent(Player damager, LivingEntity damagee, double damage) {
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
	 * @return the entity who was hurt by the damager
	 */
	public LivingEntity getDamagee() {
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
	 * @param damage the amount of damage the damager has caused the entity
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