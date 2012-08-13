/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import sun.awt.util.IdentityArrayList;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 17, 2011
 */
public class CombinedInventoryListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryOpen(InventoryOpenEvent event) {
		List<Inventory> inventories = new ArrayList<Inventory>();
		if (event.getInventory().getType() == InventoryType.CHEST) {
			HumanEntity player = event.getPlayer();
			Block block = player.getTargetBlock(null, 5);
			IdentityArrayList<Block> foundChests = new IdentityArrayList<Block>() {
				@Override
				public boolean contains(Object obj) {
					if (obj instanceof Block) {
						for (Block block : this) {
							if (block.getLocation().equals(((Block)obj).getLocation())){
								return true;
							}
						}
					}
					return false;
				}
			};
			if (block.getState() instanceof Chest) {
				inventories.addAll(findChests(block, foundChests));
			}
			player.openInventory(new CombinedInventoryView(player, inventories));

		}
	}

	private List<Inventory> findChests(Block block, IdentityArrayList<Block> foundChests) {
		List<Inventory> inventories = new ArrayList<Inventory>();

		for (BlockFace blockFace : BlockFace.values()) {
			Block rBlock = block.getRelative(blockFace);

			if (!foundChests.contains(rBlock) && rBlock.getState() instanceof Chest) {
				foundChests.add(block);
				inventories.addAll(findChests(block.getRelative(blockFace), foundChests));
			}
		}

		if (block.getState() instanceof Chest) {
			inventories.add(((Chest) block.getState()).getInventory());
		}

		return inventories;
	}
}
