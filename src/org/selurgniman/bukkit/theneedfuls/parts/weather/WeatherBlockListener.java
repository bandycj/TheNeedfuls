/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.weather;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 30, 2011
 */
public class WeatherBlockListener extends BlockListener {
	@Override
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {

		if (!event.isCancelled() && event.getDirection() == BlockFace.UP) {
			Block piston = event.getBlock();

			Block aboveBlock = piston.getRelative(BlockFace.UP, 2);
			Block aboveBlock2 = piston.getRelative(BlockFace.UP, 3);
			Block aboveBlock3 = piston.getRelative(BlockFace.UP, 4);

			boolean valid = (aboveBlock.getType() == Material.IRON_FENCE && aboveBlock2.getType() == Material.IRON_FENCE && aboveBlock3.getType() == Material.IRON_FENCE);
			if (valid) {
				for (BlockFace blockFace : TheNeedfuls.BLOCKFACES) {
					if (aboveBlock3.getRelative(blockFace).getType() != Material.IRON_FENCE) {
						valid = false;
						break;
					}
				}
			}
			if (valid) {
				piston.getWorld().strikeLightningEffect(aboveBlock3.getLocation());
				piston.getWorld().setWeatherDuration(0);
				piston.getWorld().setStorm(false);
			}
		}
	}
}
