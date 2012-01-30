/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.torch;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * Created on: Dec 30, 2011
 */
public class TorchListener implements Listener {

	private final Model pluginModel;
	private final TorchModel torchModel;
	
	public TorchListener(Model pluginModel) {
		this.pluginModel = pluginModel;
		this.torchModel = (TorchModel) Model.getCommandModel(CommandType.TORCH);

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(final BlockPlaceEvent event) {
		if (!event.isCancelled()) {
			Block placedBlock = event.getBlockPlaced();
			if (placedBlock.getType() == Material.TORCH && pluginModel.isCommandWorld(CommandType.TORCH, placedBlock.getWorld())) {
				torchModel.addTorch(event.getBlock());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			Block block = event.getBlock();
			if (block.getType() == Material.TORCH && pluginModel.isCommandWorld(CommandType.TORCH, block.getWorld())) {
				torchModel.removeTorch(event.getBlock());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (!event.isCancelled()) {
			Block block = event.getBlock();
			if (event.getCause() == IgniteCause.FLINT_AND_STEEL && pluginModel.isCommandWorld(CommandType.TORCH, block.getWorld())) {
				if (block.getRelative(BlockFace.DOWN).getType() == Material.NETHERRACK) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!event.isCancelled() && event.blockList().size() > 0 && pluginModel.isCommandWorld(CommandType.TORCH, event.blockList().get(0).getWorld())) {
			for (Block block : event.blockList()) {
				if (block.getType() == Material.TORCH) {
					torchModel.removeTorch(block);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryCraft(InventoryCraftEvent event) {
		if (!event.isCancelled() && event.getResult() != null) {
			Material type = event.getResult().getType();
			if (type == Material.GLOWSTONE || type == Material.PUMPKIN_SEEDS) {
				if (pluginModel.isCommandWorld(CommandType.TORCH, event.getPlayer().getWorld())) {
					event.getPlayer().sendMessage(Message.TORCH_DENY_MESSAGE.toString());
					event.setCancelled(true);
				}
			}
		}
	}
}
