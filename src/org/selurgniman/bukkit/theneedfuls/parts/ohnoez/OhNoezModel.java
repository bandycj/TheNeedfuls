/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.ohnoez;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.model.AbstractCommandModel;
import org.selurgniman.bukkit.theneedfuls.model.dao.Credit;
import org.selurgniman.bukkit.theneedfuls.model.dao.InventoryEnchant;
import org.selurgniman.bukkit.theneedfuls.model.dao.InventoryItem;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 27,
 *         2011
 */
public class OhNoezModel extends AbstractCommandModel {

	public void useAvailableCredit(Player player) {
		if (getPlugin() == null)
			return;

		Credit creditClass = getRecordForPlayer(player);
		if (getAvailableCredits(player) > 0) {
			creditClass.setCredits(creditClass.getCredits() - 1);
			creditClass.setLastCredit(new Date());
			getPlugin().getDatabase().delete(creditClass.getInventoryItems());
			getPlugin().getDatabase().save(creditClass);
			player.getServer().broadcastMessage(Message.CREDIT_USED_MESSAGE.with(player.getName()));
		}
	}

	public int getAvailableCredits(Player player) {
		if (getPlugin() == null)
			return -1;

		Credit creditClass = getRecordForPlayer(player);
		if (creditClass.getCredits() == 0) {
			Date lastCreditDate = creditClass.getLastCredit();
			int duration = (int) ((new Date().getTime() - lastCreditDate.getTime()) / 1000);
			if (duration >= getPlugin().getConfig().getInt("frequency", 86400)) {
				creditClass.setCredits(getPlugin().getConfig().getInt("credits", 1));
				getPlugin().getDatabase().save(creditClass);
			}
		}

		return creditClass.getCredits();
	}

	public SimpleEntry<Integer, Float> getLastExperience(Player player) {
		SimpleEntry<Integer, Float> entry = null;
		Credit creditClass = getRecordForPlayer(player);
		for (InventoryItem drop : creditClass.getInventoryItems()) {
			if (drop.getItemId() == -2) {
				player.sendMessage("level: " + drop.getItemCount() + " next: " + drop.getItemDurability());
				entry = new SimpleEntry<Integer, Float>(drop.getItemCount(), drop.getItemDurability().floatValue() / 100);
				player.sendMessage("level: " + entry.getKey() + " next: " + entry.getValue());
				break;
			}
		}

		return entry;
	}

	public List<ItemStack> getLastInventory(Player player) {
		Credit creditClass = getRecordForPlayer(player);
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (InventoryItem drop : creditClass.getInventoryItems()) {
			if (drop.getItemId() != -2 && player.getWorld().getUID().equals(UUID.fromString(drop.getWorldUuid()))) {
				Material material = Material.getMaterial(drop.getItemId());
				int itemCount = drop.getItemCount();
				short itemDurability = drop.getItemDurability().shortValue();
				byte itemData = drop.getItemData().byteValue();
				List<InventoryEnchant> enchants = drop.getEnchants();

				ItemStack item = new ItemStack(material, itemCount, itemDurability, itemData);
				for (InventoryEnchant enchant : enchants) {
					Enchantment enchantment = Enchantment.getById(enchant.getEnchantId());
					item.addEnchantment(enchantment, enchant.getEnchantLevel());
				}
				items.add(item);
			}
		}
		return items;
	}

	public void addAvailableCredits(Player player, int count) {
		setAvailableCredits(player, getAvailableCredits(player) + count);
	}

	public void setAvailableCredits(Player player, int count) {
		if (getPlugin() == null)
			return;

		Credit creditClass = getRecordForPlayer(player);
		creditClass.setCredits(count);

		getPlugin().getDatabase().save(creditClass);
	}

	public void setLastInventory(PlayerDeathEvent event) {
		try {
			if (getPlugin() == null)
				return;

			Player player = (Player) event.getEntity();
			Credit creditClass = getRecordForPlayer(player);
			ArrayList<InventoryItem> drops = new ArrayList<InventoryItem>();
			int itemId = -2;
			int itemCount = player.getLevel();
			int itemData = 0;
			int itemDurability = event.getDroppedExp();
			// Weird bukkit bug when xp is manually adjust it reports progress
			// toward the next level as 100 when it should be 0.
			if (itemDurability == 100) {
				itemDurability = 0;
			}
			String worldId = player.getWorld().getUID().toString();
			drops.add(new InventoryItem(creditClass, itemId, itemCount, itemData, itemDurability, -99, worldId));

			int counter = 0;
			for (ItemStack item : event.getDrops()) {
				itemId = item.getTypeId();
				itemCount = item.getAmount();
				itemData = item.getData().getData();
				itemDurability = item.getDurability();
				InventoryItem drop = new InventoryItem(creditClass, itemId, itemCount, itemData, itemDurability, counter, worldId);

				ArrayList<InventoryEnchant> enchants = new ArrayList<InventoryEnchant>();
				for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
					int id = entry.getKey().getId();
					int level = entry.getValue();
					enchants.add(new InventoryEnchant(id, level, drop));
				}
				drop.setEnchants(enchants);

				drops.add(drop);
				counter++;
			}

			getPlugin().getDatabase().save(drops);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Credit getRecordForPlayer(Player player) {
		if (getPlugin() == null)
			return null;
		try {
			String name = player.getName();
			Credit creditClass = getPlugin().getDatabase().find(Credit.class).where().ieq("player_name", name).findUnique();
			if (creditClass == null) {
				creditClass = new Credit();
				creditClass.setPlayer(player);
				creditClass.setCredits(getPlugin().getConfig().getInt("credits", 1));

				getPlugin().getDatabase().save(creditClass);
			}

			return creditClass;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getPlayerDamageCause(EntityDamageEvent event) {
		// ****************************************************************
		Player player = (Player) event.getEntity();
		String cause = "";
		// ****************************************************************

		if (event instanceof EntityDamageByBlockEvent) {
			EntityDamageByBlockEvent evt = (EntityDamageByBlockEvent) event;
			if (evt.getDamager() != null) {
				cause = evt.getDamager().getType().toString();
			} else {
				Material material = player.getLocation().getBlock().getType();
				if (material == Material.LAVA || material == Material.STATIONARY_LAVA) {
					cause = "LAVA";
				}
			}
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) event;

			if (evt.getDamager() instanceof Monster) {
				cause = ((Monster) evt.getDamager()).toString();
			} else if (evt.getDamager() instanceof Player) {
				cause = ((Player) evt.getDamager()).getName();
			} else if (evt.getDamager() instanceof Projectile) {
				cause = ((Projectile) evt.getDamager()).getShooter().toString();
			}
		} else if (event.getCause() == DamageCause.FIRE_TICK) {
			cause = "FIRE";
		} else {
			cause = event.getCause().toString();
		}

		String configCause = Message.valueOf(cause).toString();
		if (configCause != null) {
			cause = configCause;
		}

		return cause.replace("Craft", "");
	}
}
