/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.misc;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 *
 */
public class DropEnhancerListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			Block block = event.getBlock();
			if (block.getType() == Material.GLASS || block.getType() == Material.THIN_GLASS) {
				Random r = new Random();
				int glassChance = r.nextInt(3);
				int sandChance = r.nextInt(2);

				if (glassChance > 0) {
					block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType(), 1));
				} else if (sandChance > 0 && block.getType() == Material.GLASS) {
					block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SAND, 1));
				}
			} else if (block.getType() == Material.LEAVES){
				Random r = new Random();
				int appleChance = r.nextInt(10);
				if (appleChance>8){
					block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.APPLE,1));
				}
			}
		}
	}
}
