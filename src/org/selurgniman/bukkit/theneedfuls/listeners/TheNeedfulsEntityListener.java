/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 17, 2011
 */
public class TheNeedfulsEntityListener extends EntityListener {
	private final Model model;
	

	public TheNeedfulsEntityListener(Model model) {
		this.model = model;
	}

	@Override
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!event.isCancelled() && event.blockList().size() > 0 && model.isCommandWorld(CommandType.TORCH, event.blockList().get(0).getWorld())) {
			for (Block block : event.blockList()) {
				if (block.getType() == Material.TORCH) {
					model.removeTorch(block);
				}
			}
		}
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		if (event instanceof PlayerDeathEvent && model.isCommandWorld(CommandType.OHNOEZ, event.getEntity().getWorld())) {
			Player player = (Player) event.getEntity();
			String cause = getPlayerDamageCause(player.getLastDamageCause());

			PlayerDeathEvent playerDeathEvent = (PlayerDeathEvent) event;
			playerDeathEvent.setDeathMessage(String.format(Message.OTHER_PLAYER_DEATH_MESSAGE.toString(), player.getName(), cause));

			model.setLastInventory(playerDeathEvent);
		}
	}

	private String getPlayerDamageCause(EntityDamageEvent event) {
		// ****************************************************************
		Player player = (Player) event.getEntity();
		String cause = "";
		// ****************************************************************

		if (event instanceof EntityDamageByBlockEvent) {
			EntityDamageByBlockEvent evt = (EntityDamageByBlockEvent) event;
			if (evt.getDamager() != null) {
				cause = evt.getDamager().getType().toString();
			} else {
				Material material = player.getLocation().getBlock().getType();
				if (material == Material.LAVA || material == Material.STATIONARY_LAVA) {
					cause = "LAVA";
				}
			}
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) event;

			if (evt.getDamager() instanceof Monster) {
				cause = ((Monster) evt.getDamager()).toString();
			} else if (evt.getDamager() instanceof Player) {
				cause = ((Player) evt.getDamager()).getName();
			} else if (evt.getDamager() instanceof Projectile) {
				cause = ((Projectile) evt.getDamager()).getShooter().toString();
			}
		} else if (event.getCause() == DamageCause.FIRE_TICK) {
			cause = "FIRE";
		} else {
			cause = event.getCause().toString();
		}

		String configCause = Message.valueOf(cause).toString();
		if (configCause != null) {
			cause = configCause;
		}

		return cause.replace("Craft", "");
	}
}
