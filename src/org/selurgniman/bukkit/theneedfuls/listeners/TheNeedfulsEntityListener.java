/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;
import org.selurgniman.bukkit.theneedfuls.model.OhNoezModel;
import org.selurgniman.bukkit.theneedfuls.model.TorchModel;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 17, 2011
 */
public class TheNeedfulsEntityListener extends EntityListener {
	private final Model model;
	private final TorchModel torchModel;
	private final OhNoezModel ohnoezModel;

	public TheNeedfulsEntityListener(Model model) {
		this.model = model;
		this.torchModel = (TorchModel)Model.getCommandModel(CommandType.TORCH);
		this.ohnoezModel = (OhNoezModel)Model.getCommandModel(CommandType.OHNOEZ);
	}

	@Override
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!event.isCancelled() && event.blockList().size() > 0 && model.isCommandWorld(CommandType.TORCH, event.blockList().get(0).getWorld())) {
			for (Block block : event.blockList()) {
				if (block.getType() == Material.TORCH) {
					torchModel.removeTorch(block);
				}
			}
		}
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
