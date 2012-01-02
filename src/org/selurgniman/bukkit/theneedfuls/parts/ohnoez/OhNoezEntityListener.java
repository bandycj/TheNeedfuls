/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.ohnoez;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 17, 2011
 */
public class OhNoezEntityListener extends EntityListener {
	private final Model model;
	private final OhNoezModel ohnoezModel;

	public OhNoezEntityListener(Model model) {
		this.model = model;
		this.ohnoezModel = (OhNoezModel)Model.getCommandModel(CommandType.OHNOEZ);
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		if (event instanceof PlayerDeathEvent && model.isCommandWorld(CommandType.OHNOEZ, event.getEntity().getWorld())) {
			Player player = (Player) event.getEntity();
			String cause = ohnoezModel.getPlayerDamageCause(player.getLastDamageCause());

			PlayerDeathEvent playerDeathEvent = (PlayerDeathEvent) event;
			playerDeathEvent.setDeathMessage(String.format(Message.OTHER_PLAYER_DEATH_MESSAGE.toString(), player.getName(), cause));

			ohnoezModel.setLastInventory(playerDeathEvent);
		}
	}
}
