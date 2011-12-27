/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 27,
 *         2011
 */
public class WorldsModel extends AbstractCommandModel {

	public void storeWorldInventory(World world, Player player) {
		if (getPlugin() == null)
			return;
		ArrayList<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
		int itemId = -2;
		int itemCount = player.getLevel();
		int itemData = player.getTotalExperience();
		int itemDurability = Math.round(player.getExp());
		// Weird bukkit bug when xp is manually adjust it reports progress
		// toward the next level as 100 when it should be 0.
		if (itemDurability == 100) {
			itemDurability = 0;
		}
		String worldId = player.getWorld().getUID().toString();
		inventoryItems.add(new InventoryItem(player.getName(), itemId, itemCount, itemData, itemDurability, -98, worldId));

		int counter = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null) {
				inventoryItems.add(createInventoryItem(item, counter, player, worldId));
			}
			counter++;
		}
		inventoryItems.add(createInventoryItem(player.getInventory().getHelmet(), -1, player, worldId));
		inventoryItems.add(createInventoryItem(player.getInventory().getChestplate(), -2, player, worldId));
		inventoryItems.add(createInventoryItem(player.getInventory().getLeggings(), -3, player, worldId));
		inventoryItems.add(createInventoryItem(player.getInventory().getBoots(), -4, player, worldId));

		getPlugin().getDatabase().save(inventoryItems);
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
		player.getInventory().setChestplate(new ItemStack(Material.AIR));
		player.getInventory().setLeggings(new ItemStack(Material.AIR));
		player.getInventory().setBoots(new ItemStack(Material.AIR));
		player.setTotalExperience(0);
	}

	public void restoreWorldInventory(World world, Player player) {
		if (getPlugin() == null)
			return;

		List<InventoryItem> inventoryItems = getInventoryItems(world, player);
		for (InventoryItem inventoryItem : inventoryItems) {
			if (inventoryItem.getItemId() == -2) {
//				player.setLevel(inventoryItem.getItemCount());
//				player.setExp(inventoryItem.getItemDurability().floatValue() / 100);
				player.setTotalExperience(inventoryItem.getItemData());
			} else {
				ItemStack item = createItemStack(inventoryItem);
				System.out.println("item:" + item);
				if (item != null) {
					if (inventoryItem.getItemSlot() > -1) {
						player.getInventory().addItem(item);
					} else if (inventoryItem.getItemSlot() < 0) {
						switch (inventoryItem.getItemSlot()) {
							case -1: {
								player.getInventory().setHelmet(item);
								break;
							}
							case -2: {
								player.getInventory().setChestplate(item);
								break;
							}
							case -3: {
								player.getInventory().setLeggings(item);
								break;
							}
							case -4: {
								player.getInventory().setBoots(item);
								break;
							}
						}
					}
				}
			}
		}
		getPlugin().getDatabase().delete(inventoryItems);
	}

	private InventoryItem createInventoryItem(ItemStack item, int slot, Player player, String worldId) {
		int itemId = item.getTypeId();
		int itemCount = item.getAmount();
		int itemData = item.getData().getData();
		int itemDurability = item.getDurability();
		InventoryItem inventoryItem = new InventoryItem(player.getName(), itemId, itemCount, itemData, itemDurability, slot, worldId);

		ArrayList<InventoryEnchant> enchants = new ArrayList<InventoryEnchant>();
		for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
			int id = entry.getKey().getId();
			int level = entry.getValue();
			enchants.add(new InventoryEnchant(id, level, inventoryItem));
		}
		inventoryItem.setEnchants(enchants);

		return inventoryItem;
	}

	private ItemStack createItemStack(InventoryItem inventoryItem) {
		Material material = Material.getMaterial(inventoryItem.getItemId());
		int itemCount = inventoryItem.getItemCount();
		short itemDurability = inventoryItem.getItemDurability().shortValue();
		byte itemData = inventoryItem.getItemData().byteValue();
		List<InventoryEnchant> enchants = inventoryItem.getEnchants();
		if (itemCount > 0 && itemCount <= 64) {
			ItemStack item = new ItemStack(material, itemCount, itemDurability, itemData);
			for (InventoryEnchant enchant : enchants) {
				Enchantment enchantment = Enchantment.getById(enchant.getEnchantId());
				item.addEnchantment(enchantment, enchant.getEnchantLevel());
			}
			return item;
		}
		return null;
	}

	private List<InventoryItem> getInventoryItems(World world, Player player) {
		if (getPlugin() == null)
			return new ArrayList<InventoryItem>();

		String id = world.getUID().toString();
		String playerName = player.getName();
		List<InventoryItem> inventoryItems = getPlugin()
				.getDatabase()
				.find(InventoryItem.class)
				.where()
				.ieq("worldUuid", id)
				.ieq("player", playerName)
				.findList();
		if (inventoryItems == null) {
			inventoryItems = new ArrayList<InventoryItem>();
		}

		return inventoryItems;
	}
}
