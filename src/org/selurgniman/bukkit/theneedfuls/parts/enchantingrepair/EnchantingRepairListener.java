/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.enchantingrepair;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Jan 7, 2012
 */
public class EnchantingRepairListener implements Listener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onCraftItem(CraftItemEvent event) {
		if (!event.isCancelled() && event.getCurrentItem() != null && event.getInventory() != null && event.getWhoClicked() instanceof Player) {
			Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
			for (ItemStack item : event.getInventory().getContents()) {
				if (item != null) {
					enchants.putAll(item.getEnchantments());
				}
			}
			if (enchants.size() > 0) {
				ItemStack result = event.getCurrentItem();
				TheNeedfuls.debug("created item to enchant!" + result);
				result.addEnchantments(enchants);
				TheNeedfuls.debug("applied enchantement to " + result);
			}
		}
	}
}
