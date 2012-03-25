/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.worlds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.selurgniman.bukkit.theneedfuls.model.AbstractCommandModel;
import org.selurgniman.bukkit.theneedfuls.model.dao.InventoryEnchant;
import org.selurgniman.bukkit.theneedfuls.model.dao.InventoryItem;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 27,
 *         2011
 */
public class WorldsModel extends AbstractCommandModel {

	public void storeWorldInventory(World world, Player player) {
		if (getPlugin() == null)
			return;

		PlayerInventory inventory = player.getInventory();
		ArrayList<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
		int itemId = -2;
		int itemCount = player.getLevel();
		int itemData = player.getTotalExperience();
		int itemDurability = Math.round(player.getExp() * 100);
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
				InventoryItem inventoryItem = createInventoryItem(item, counter, player, worldId);
				if (inventoryItem != null){
					inventoryItems.add(inventoryItem);
				}
			}
			counter++;
		}
		InventoryItem helmet = createInventoryItem(inventory.getHelmet(), -4, player, worldId);
		InventoryItem chest = createInventoryItem(inventory.getChestplate(), -3, player, worldId);
		InventoryItem legs = createInventoryItem(inventory.getLeggings(), -2, player, worldId);
		InventoryItem boots = createInventoryItem(inventory.getBoots(), -1, player, worldId);
		
		if (helmet != null){
			inventoryItems.add(helmet);
		}
		if (chest != null){
			inventoryItems.add(chest);
		}
		if (legs != null){
			inventoryItems.add(legs);
		}
		if (boots != null){
			inventoryItems.add(boots);
		}
			

		getPlugin().getDatabase().save(inventoryItems);
		inventory.clear();
		inventory.setArmorContents(null);
		player.setLevel(0);
		player.setExp(0.0f);
	}

	public void restoreWorldInventory(World world, Player player) {
		if (getPlugin() == null)
			return;

		PlayerInventory inventory = player.getInventory();
		ItemStack[] inventories = new ItemStack[inventory.getSize()];
		ItemStack[] armors = new ItemStack[4];
		List<InventoryItem> inventoryItems = getInventoryItems(world, player);
		for (InventoryItem inventoryItem : inventoryItems) {
			if (inventoryItem.getItemId() == -2) {
				player.setLevel(inventoryItem.getItemCount());
				player.setExp(inventoryItem.getItemDurability().floatValue() / 100);
			} else {
				ItemStack item = createItemStack(inventoryItem);
				if (item != null) {
					if (inventoryItem.getItemSlot() > -1) {
						inventories[inventoryItem.getItemSlot()] = item;
					} else if (inventoryItem.getItemSlot() < 0) {
						armors[inventoryItem.getItemSlot() + 4] = item;
					}
				}
			}
		}
		inventory.setContents(inventories);
		inventory.setArmorContents(armors);
		getPlugin().getDatabase().delete(inventoryItems);
	}

	private InventoryItem createInventoryItem(ItemStack item, int slot, Player player, String worldId) {
		if (item != null && player != null && worldId != null && !worldId.isEmpty()) {
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
		return null;
	}

	private ItemStack createItemStack(InventoryItem inventoryItem) {
		Material material = Material.getMaterial(inventoryItem.getItemId());
		int itemCount = inventoryItem.getItemCount();
		short itemDurability = inventoryItem.getItemDurability().shortValue();
		byte itemData = inventoryItem.getItemData().byteValue();
		List<InventoryEnchant> enchants = inventoryItem.getEnchants();
		if (itemCount > 0 && itemCount <= 64) {
			ItemStack item = new ItemStack(material, itemCount, itemDurability, itemData);
			HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
			for (InventoryEnchant enchant : enchants) {
				enchantments.put(Enchantment.getById(enchant.getEnchantId()), enchant.getEnchantLevel());
			}
			item.addEnchantments(enchantments);
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
				.ieq("credit_id", null)
				.findList();
		if (inventoryItems == null) {
			inventoryItems = new ArrayList<InventoryItem>();
		}

		return inventoryItems;
	}
}
