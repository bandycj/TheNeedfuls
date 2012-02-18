/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.misc;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * 
 */
public class SignPlacerListener implements Listener {
	private final static Material[] placeableMaterials = {
			Material.COBBLESTONE,
			Material.STONE,
			Material.SANDSTONE,
			Material.GLASS,
			Material.DIRT,
			Material.SAND,
			Material.GRAVEL
	};
	private final static Material[] replaceableMaterials = {
			Material.AIR,
			Material.WATER,
			Material.LAVA
	};
	static {
		Arrays.sort(placeableMaterials);
		Arrays.sort(replaceableMaterials);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void signPlaced(PlayerInteractEvent evt) {
		ItemStack heldItem = evt.getItem();
		if (!evt.isCancelled() && heldItem != null && heldItem.getType() == Material.SIGN) {
			Block clickedBlock = evt.getClickedBlock();
			Block replaceBlock = clickedBlock.getRelative(evt.getBlockFace());

			Material clickedBlockType = clickedBlock.getType();
			Material replaceMaterial = replaceBlock.getType();

			if (Arrays.binarySearch(replaceableMaterials, replaceMaterial) > -1 && Arrays.binarySearch(placeableMaterials, clickedBlockType) > -1) {
				replaceBlock.setType(Material.WALL_SIGN);
				replaceBlock.setData(getData(evt.getBlockFace()));

				if (heldItem.getAmount() > 1) {
					heldItem.setAmount(heldItem.getAmount() - 1);
				} else {
					evt.getPlayer().setItemInHand(null);
				}
			}
		}
	}

	private Byte getData(BlockFace blockFace) {
		switch (blockFace) {
			case EAST:
				return 0x2;
			case WEST:
				return 0x3;
			case NORTH:
				return 0x4;
			case SOUTH:
			default:
				return 0x5;
		}
	}
}
