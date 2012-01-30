/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.ohnoez;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 17, 2011
 */
public class OhNoezListener implements Listener {
	private final Model model;
	private final OhNoezModel ohnoezModel;

	public OhNoezListener(Model model) {
		this.model = model;
		this.ohnoezModel = (OhNoezModel)Model.getCommandModel(CommandType.OHNOEZ);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		if (event instanceof PlayerDeathEvent && model.isCommandWorld(CommandType.OHNOEZ, event.getEntity().getWorld())) {
			Player player = (Player) event.getEntity();
			String cause = ohnoezModel.getPlayerDamageCause(player.getLastDamageCause());

			PlayerDeathEvent playerDeathEvent = (PlayerDeathEvent) event;
			playerDeathEvent.setDeathMessage(Message.OTHER_PLAYER_DEATH_MESSAGE.with(player.getName(), cause));

			ohnoezModel.setLastInventory(playerDeathEvent);
		}
	}
}
