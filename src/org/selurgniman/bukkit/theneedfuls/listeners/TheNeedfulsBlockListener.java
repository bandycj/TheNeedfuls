/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.model.Model;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 14, 2011
 */
public class TheNeedfulsBlockListener extends BlockListener {
	private final Model model;

	public TheNeedfulsBlockListener(TheNeedfuls plugin) {
		this.model = plugin.getModel();

	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.isCancelled()) {
			Block placedBlock = event.getBlockPlaced();
			if (placedBlock.getType() == Material.TORCH && model.isTorchWorld(placedBlock.getWorld())) {
				model.addTorch(event.getBlock());
			}
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			Block block = event.getBlock();
			if (block.getType() == Material.TORCH && model.isTorchWorld(block.getWorld())) {
				model.removeTorch(event.getBlock());
			}
		}
	}

	@Override
	public void onBlockDispense(BlockDispenseEvent event) {
		if (event.getItem().getType() == Material.WATER_BUCKET) {
			if (event.getBlock().getRelative(BlockFace.DOWN).getType() == Material.SNOW_BLOCK) {
				event.setItem(new ItemStack(Material.ICE, model.getConfig().getInt("iceMaker.quantity")));
			}
		}
	}
}