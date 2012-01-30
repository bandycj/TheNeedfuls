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
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Jan 7, 2012
 */
public class EnchantingRepairListener implements Listener{

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryCraft(InventoryCraftEvent event) {
		if (!event.isCancelled() && event.getResult() != null && event.getInventory() != null && !(event.getInventory() instanceof PlayerInventory)) {
			Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
			for (ItemStack item : event.getInventory().getContents()) {
				if (item != null) {
					enchants.putAll(item.getEnchantments());
				}
			}
			if (enchants.size() > 0) {
				ItemStack result = event.getResult();
				result.addEnchantments(enchants);
				EnchantingRepairModel.addEnchantRepair(event.getPlayer(), result);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onItemHeldChange(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		if (EnchantingRepairModel.hasStoredEnchants(player)) {
			ItemStack heldItem = player.getInventory().getItem(event.getNewSlot());
			ItemStack item = EnchantingRepairModel.getEnchantRepair(player, heldItem);
			if (item != null && heldItem != null) {
				if (item.getDurability() == heldItem.getDurability() && item.getType() == heldItem.getType() && item.getAmount() == heldItem.getAmount()) {
					heldItem.addEnchantments(item.getEnchantments());
					EnchantingRepairModel.enchantApplied(player, item);
				}
			}
		}
	}
}
