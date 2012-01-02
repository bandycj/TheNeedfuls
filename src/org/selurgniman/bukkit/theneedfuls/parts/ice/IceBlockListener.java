/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.ice;

import org.bukkit.event.block.BlockListener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * Created on: Dec 30, 2011
 */
public class IceBlockListener extends BlockListener {

	private final TheNeedfuls plugin;
	public IceBlockListener(TheNeedfuls plugin){
		this.plugin = plugin;
	}
	
	@Override
	public void onBlockDispense(BlockDispenseEvent event) {
		if (!event.isCancelled() && event.getItem().getType() == Material.WATER_BUCKET) {
			Block block = event.getBlock();
			if (block.getRelative(BlockFace.DOWN).getType() == Material.SNOW_BLOCK) {
				event.setItem(new ItemStack(Material.ICE, plugin.getConfig().getInt("iceMaker.quantity")));
				block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.BUCKET, 1));
			}
		}
	}
}
