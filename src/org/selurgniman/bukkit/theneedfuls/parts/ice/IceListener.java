/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.ice;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.selurgniman.bukkit.theneedfuls.model.Model;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * Created on: Dec 30, 2011
 */
public class IceListener implements Listener {

	private final Model pluginModel;
	public IceListener(Model pluginModel){
		this.pluginModel = pluginModel;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockDispense(BlockDispenseEvent event) {
		if (!event.isCancelled() && event.getItem().getType() == Material.WATER_BUCKET) {
			Block block = event.getBlock();
			if (block.getRelative(BlockFace.DOWN).getType() == Material.SNOW_BLOCK) {
				event.setItem(new ItemStack(Material.ICE, pluginModel.getConfig().getInt("iceMaker.quantity")));
				block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.BUCKET, 1));
			}
		}
	}
}
