/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 14,
 *         2011
 */
public class BlockPlacedListener extends BlockListener {
	private final Model model;

	public BlockPlacedListener(Model model) {
		this.model = model;
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		System.out.println(event.getBlockPlaced());
		if (!event.isCancelled()) {
			Block placedBlock = event.getBlockPlaced();
			if (placedBlock.getType() == Material.TORCH) {
				model.addTorch(event.getBlock());
			} else if (placedBlock.getType() == Material.WATER && event.getBlockAgainst().getType() == Material.IRON_BLOCK
					&& event.getBlockAgainst().getRelative(BlockFace.DOWN).getType() == Material.SNOW_BLOCK) {
				placedBlock.setType(Material.AIR);
				
				event.getPlayer().getWorld().dropItemNaturally(placedBlock.getLocation(), new ItemStack(Material.ICE,1));
			}
		}
	}
}