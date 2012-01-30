/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.enchantingrepair;

import java.util.IdentityHashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import sun.awt.util.IdentityArrayList;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * 
 */
public class EnchantingRepairModel {

	private static IdentityHashMap<Player, IdentityArrayList<ItemStack>> enchantedRepairs = new IdentityHashMap<Player, IdentityArrayList<ItemStack>>();

	public static boolean hasStoredEnchants(Player player){
		if (enchantedRepairs.get(player) != null){
			return (enchantedRepairs.get(player).size() > 0);
		}
		
		return false;
	}
	
	public synchronized static void addEnchantRepair(Player player, ItemStack itemStack) {
		if (!enchantedRepairs.containsKey(player)) {
			enchantedRepairs.put(player, new IdentityArrayList<ItemStack>());
		}
		
		enchantedRepairs.get(player).add(itemStack);
	}

	public synchronized static ItemStack getEnchantRepair(Player player, ItemStack itemStack) {
		if (!enchantedRepairs.containsKey(player)) {
			enchantedRepairs.put(player, new IdentityArrayList<ItemStack>());
		} else {
			for (ItemStack item : enchantedRepairs.get(player)) {
				if (item.getDurability() == itemStack.getDurability() && item.getType() == itemStack.getType()) {
					return item;
				}
			}
		}
		return null;
	}

	public synchronized static void enchantApplied(Player player, ItemStack itemStack) {
		enchantedRepairs.get(player).remove(itemStack);
	}
}
