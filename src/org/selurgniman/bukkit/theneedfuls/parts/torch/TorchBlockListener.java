/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.torch;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * Created on: Dec 30, 2011
 */
public class TorchBlockListener extends BlockListener {

	private final TheNeedfuls plugin;
	private final TorchModel torchModel;
	
	public TorchBlockListener(TheNeedfuls plugin) {
		this.plugin = plugin;
		this.torchModel = (TorchModel) Model.getCommandModel(CommandType.TORCH);

	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.isCancelled()) {
			Block placedBlock = event.getBlockPlaced();
			if (placedBlock.getType() == Material.TORCH && plugin.getModel().isCommandWorld(CommandType.TORCH, placedBlock.getWorld())) {
				torchModel.addTorch(event.getBlock());
			}
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			Block block = event.getBlock();
			if (block.getType() == Material.TORCH && plugin.getModel().isCommandWorld(CommandType.TORCH, block.getWorld())) {
				torchModel.removeTorch(event.getBlock());
			}
		}
	}
	
	@Override
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (!event.isCancelled()) {
			Block block = event.getBlock();
			if (event.getCause() == IgniteCause.FLINT_AND_STEEL && plugin.getModel().isCommandWorld(CommandType.TORCH, block.getWorld())) {
				if (block.getRelative(BlockFace.DOWN).getType() == Material.NETHERRACK) {
					event.setCancelled(true);
				}
			}
		}
	}
	
}
