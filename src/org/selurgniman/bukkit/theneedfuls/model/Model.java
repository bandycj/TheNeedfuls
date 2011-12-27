/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.model;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;

import com.avaje.ebean.EbeanServer;
import com.lennardf1989.bukkitex.MyDatabase;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 14, 2011
 */
public class Model {
	private final MyDatabase myDatabase;
	private final EbeanServer database;
	private final JavaPlugin plugin;

	private static final String DATABASE_DRIVER = "database.driver";
	private static final String DATABASE_URL = "database.url";
	private static final String DATABASE_USER_NAME = "database.username";
	private static final String DATABASE_PASSWORD = "database.password";
	private static final String DATABASE_ISOLATION = "database.isolation";
	private static final String DATABASE_LOGGING = "database.logging";
	private static final String DATABASE_REBUILD = "database.rebuild";
	private static final HashMap<String, Object> CONFIG_DEFAULTS = new HashMap<String, Object>();
	static {
		CONFIG_DEFAULTS.put(DATABASE_DRIVER, "org.sqlite.JDBC");
		CONFIG_DEFAULTS.put(DATABASE_URL, "");
		CONFIG_DEFAULTS.put(DATABASE_USER_NAME, "bukkit");
		CONFIG_DEFAULTS.put(DATABASE_PASSWORD, "walrus");
		CONFIG_DEFAULTS.put(DATABASE_ISOLATION, "SERIALIZABLE");
		CONFIG_DEFAULTS.put(DATABASE_LOGGING, Boolean.FALSE);
		CONFIG_DEFAULTS.put(DATABASE_REBUILD, Boolean.TRUE);
	}

	private final IdentityHashMap<CommandType, Set<World>> commandWorlds = new IdentityHashMap<CommandType, Set<World>>();

	public Model(JavaPlugin plugin) {
		this.plugin = plugin;
		CONFIG_DEFAULTS.put(DATABASE_URL, "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/" + plugin.getDescription().getName() + ".db");

		for (Entry<String, Object> entry : CONFIG_DEFAULTS.entrySet()) {
			if (plugin.getConfig().get(entry.getKey()) == null) {
				plugin.getConfig().set(entry.getKey(), entry.getValue());
			}
		}

		this.myDatabase = new MyDatabase(plugin) {
			@Override
			protected java.util.List<Class<?>> getDatabaseClasses() {
				List<Class<?>> list = new ArrayList<Class<?>>();
				list.add(Torch.class);
				list.add(Credit.class);
				list.add(Drop.class);
				list.add(Enchant.class);
				list.add(InventoryItem.class);
				list.add(InventoryEnchant.class);
				return list;
			};
		};
		this.myDatabase.initializeDatabase(plugin.getConfig().getString(DATABASE_DRIVER), plugin.getConfig().getString(DATABASE_URL), plugin
				.getConfig()
				.getString(DATABASE_USER_NAME), plugin.getConfig().getString(DATABASE_PASSWORD), plugin.getConfig().getString(DATABASE_ISOLATION), plugin
				.getConfig()
				.getBoolean(DATABASE_LOGGING), plugin.getConfig().getBoolean(DATABASE_REBUILD));
		plugin.getConfig().set(DATABASE_REBUILD, false);
		this.plugin.saveConfig();

		this.database = myDatabase.getDatabase();

		loadWorldControls();
	}

	public void loadWorldControls() {
		commandWorlds.clear();
		for (CommandType command : CommandType.values()) {
			commandWorlds.put(command, new HashSet<World>());
		}
		addCommandWorlds(CommandType.TORCH, Torch.TORCH_WORLDS_KEY);
		addCommandWorlds(CommandType.OHNOEZ, Credit.OHNOEZ_WORLDS_KEY);
	}

	private void addCommandWorlds(CommandType command, String key) {
		for (String worldId : plugin.getConfig().getStringList(key)) {
			commandWorlds.get(command).add(plugin.getServer().getWorld(UUID.fromString(worldId)));
		}
	}

	public EbeanServer getDatabase() {
		return this.myDatabase.getDatabase();
	}

	public boolean isCommandWorld(CommandType command, World world) {
		return commandWorlds.get(command).contains(world);
	}

	public void removeTorch(Block torch) {
		Torch torchClass = getRecordForTorch(torch);
		if (torchClass != null) {
			removeTorch(torchClass);
		}
	}

	public void addTorch(Block torch) {
		if (getRecordForTorch(torch) == null) {
			createTorch(torch);
		}
	}

	public void expireAllTorches() {
		expireTorches(new Date());
	}

	public void expireTorches() {
		Date maxAge = new Date((new Date().getTime() - (plugin.getConfig().getInt(Torch.TORCH_AGE_KEY) * 1000)));
		expireTorches(maxAge);
	}

	public void expireTorches(Date maxAge) {
		for (Torch torch : getTorchesOlderThan(maxAge)) {
			World world = this.plugin.getServer().getWorld(UUID.fromString(torch.getWorldUuid()));
			Block torchBlock = world.getBlockAt(torch.getBlockX(), torch.getBlockY(), torch.getBlockZ());
			if (torchBlock.getType() == Material.TORCH) {
				torchBlock.setType(Material.AIR);
				world.dropItemNaturally(torchBlock.getLocation(), new ItemStack(Material.TORCH, 1));
				removeTorch(torch);
			}
		}
	}

	public int getTorchCount() {
		return database.find(Torch.class).findRowCount();
	}

	public void useAvailableCredit(Player player) {
		Credit creditClass = getRecordForPlayer(player);
		if (getAvailableCredits(player) > 0) {
			creditClass.setCredits(creditClass.getCredits() - 1);
			creditClass.setLastCredit(new Date());
			database.delete(creditClass.getDrops());
			database.save(creditClass);
			player.getServer().broadcastMessage(String.format(Message.CREDIT_USED_MESSAGE.toString(), player.getName()));
		}
	}

	public int getAvailableCredits(Player player) {
		Credit creditClass = getRecordForPlayer(player);
		if (creditClass.getCredits() == 0) {
			Date lastCreditDate = creditClass.getLastCredit();
			int duration = (int) ((new Date().getTime() - lastCreditDate.getTime()) / 1000);
			if (duration >= plugin.getConfig().getInt("frequency", 86400)) {
				creditClass.setCredits(plugin.getConfig().getInt("credits", 1));
				database.save(creditClass);
			}
		}

		return creditClass.getCredits();
	}

	public SimpleEntry<Integer, Float> getLastExperience(Player player) {
		SimpleEntry<Integer, Float> entry = null;
		Credit creditClass = getRecordForPlayer(player);
		for (Drop drop : creditClass.getDrops()) {
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
		for (Drop drop : creditClass.getDrops()) {
			if (drop.getItemId() != -2 && player.getWorld().getUID().equals(UUID.fromString(drop.getWorldUuid()))) {
				Material material = Material.getMaterial(drop.getItemId());
				int itemCount = drop.getItemCount();
				short itemDurability = drop.getItemDurability().shortValue();
				byte itemData = drop.getItemData().byteValue();
				List<Enchant> enchants = drop.getEnchants();

				ItemStack item = new ItemStack(material, itemCount, itemDurability, itemData);
				for (Enchant enchant : enchants) {
					Enchantment enchantment = Enchantment.getById(enchant.getEnchantId());
					item.addEnchantment(enchantment, enchant.getEnchantLevel());
				}
				items.add(item);
			}
		}
		return items;
	}

	public void restoreWorldInventory(World world, Player player) {
		List<InventoryItem> inventoryItems = getInventoryItems(world, player);
		for (InventoryItem inventoryItem : inventoryItems) {
			if (player.getWorld().getUID().equals(UUID.fromString(inventoryItem.getWorldUuid()))) {
				if (inventoryItem.getItemId() == -2) {
					player.setLevel(inventoryItem.getItemCount());
					player.setExp(inventoryItem.getItemDurability().floatValue() / 100);
				} else if (inventoryItem.getItemSlot() > -1) {
					player.getInventory().setItem(inventoryItem.getItemSlot(), createItemStack(inventoryItem));
				} else if (inventoryItem.getItemSlot() < 0) {
					switch (inventoryItem.getItemSlot()) {
						case -1: {
							player.getInventory().setHelmet(createItemStack(inventoryItem));
							break;
						}
						case -2: {
							player.getInventory().setChestplate(createItemStack(inventoryItem));
							break;
						}
						case -3: {
							player.getInventory().setLeggings(createItemStack(inventoryItem));
							break;
						}
						case -4: {
							player.getInventory().setBoots(createItemStack(inventoryItem));
							break;
						}
					}
				}
			}
		}

		database.delete(inventoryItems);
	}

	private ItemStack createItemStack(InventoryItem inventoryItem) {
		Material material = Material.getMaterial(inventoryItem.getItemId());
		int itemCount = inventoryItem.getItemCount();
		short itemDurability = inventoryItem.getItemDurability().shortValue();
		byte itemData = inventoryItem.getItemData().byteValue();
		List<InventoryEnchant> enchants = inventoryItem.getEnchants();

		ItemStack item = new ItemStack(material, itemCount, itemDurability, itemData);
		for (InventoryEnchant enchant : enchants) {
			Enchantment enchantment = Enchantment.getById(enchant.getEnchantId());
			item.addEnchantment(enchantment, enchant.getEnchantLevel());
		}
		return item;
	}

	public void addAvailableCredits(Player player, int count) {
		setAvailableCredits(player, getAvailableCredits(player) + count);
	}

	public void setAvailableCredits(Player player, int count) {
		Credit creditClass = getRecordForPlayer(player);
		creditClass.setCredits(count);

		database.save(creditClass);
	}

	public void setLastInventory(PlayerDeathEvent event) {
		Player player = (Player) event.getEntity();
		Credit creditClass = getRecordForPlayer(player);
		ArrayList<Drop> drops = new ArrayList<Drop>();
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
		drops.add(new Drop(creditClass, itemId, itemCount, itemData, itemDurability, worldId));

		for (ItemStack item : event.getDrops()) {
			itemId = item.getTypeId();
			itemCount = item.getAmount();
			itemData = item.getData().getData();
			itemDurability = item.getDurability();
			Drop drop = new Drop(creditClass, itemId, itemCount, itemData, itemDurability, worldId);

			ArrayList<Enchant> enchants = new ArrayList<Enchant>();
			for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
				int id = entry.getKey().getId();
				int level = entry.getValue();
				enchants.add(new Enchant(id, level, drop));
			}
			drop.setEnchants(enchants);

			drops.add(drop);
		}

		database.save(drops);
	}

	public void storeWorldInventory(World world, Player player) {
		ArrayList<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
		int itemId = -2;
		int itemCount = player.getLevel();
		int itemData = 0;
		int itemDurability = Math.round(player.getExp());
		// Weird bukkit bug when xp is manually adjust it reports progress
		// toward the next level as 100 when it should be 0.
		if (itemDurability == 100) {
			itemDurability = 0;
		}
		String worldId = player.getWorld().getUID().toString();
		inventoryItems.add(new InventoryItem(player.getName(), itemId, itemCount, itemData, itemDurability, -98, worldId));

		for (int i = 0; i < 36; i++) {
			ItemStack item = player.getInventory().getItem(i);
			if (item != null) {
				inventoryItems.add(createInventoryItem(item, i, player, worldId));
			}
		}
		inventoryItems.add(createInventoryItem(player.getInventory().getHelmet(), -1, player, worldId));
		inventoryItems.add(createInventoryItem(player.getInventory().getChestplate(), -2, player, worldId));
		inventoryItems.add(createInventoryItem(player.getInventory().getLeggings(), -3, player, worldId));
		inventoryItems.add(createInventoryItem(player.getInventory().getBoots(), -4, player, worldId));
		
		database.save(inventoryItems);
		player.getInventory().clear();
		player.setTotalExperience(0);
	}

	private InventoryItem createInventoryItem(ItemStack item, int slot, Player player, String worldId) {
		int itemId = item.getTypeId();
		int itemCount = item.getAmount();
		int itemData = item.getData().getData();
		int itemDurability = item.getDurability();
		System.out.println("itemId:"+itemId+" itemCount:"+itemCount+" itemData:"+itemData+" itemDurability:"+itemDurability);
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

	private void removeTorch(Torch torch) {
		database.delete(torch);
	}

	private Torch createTorch(Block torch) {
		Torch torchClass = new Torch();
		torchClass.setBlockX(torch.getX());
		torchClass.setBlockY(torch.getY());
		torchClass.setBlockZ(torch.getZ());
		torchClass.setWorldUuid(torch.getWorld().getUID().toString());
		torchClass.setPlacedDate(new Date());

		database.save(torchClass);
		return torchClass;
	}

	private Torch getRecordForTorch(Block torch) {
		Map<String, Object> location = new HashMap<String, Object>();
		location.put("blockX", String.valueOf(torch.getX()));
		location.put("blockY", String.valueOf(torch.getY()));
		location.put("blockZ", String.valueOf(torch.getZ()));
		location.put("worldUuid", torch.getWorld().getUID().toString());

		return database.find(Torch.class).where().allEq(location).findUnique();
	}

	private List<Torch> getTorchesOlderThan(Date date) {
		return database.find(Torch.class).where().lt("placed_date", date).findList();
	}

	private Credit getRecordForPlayer(Player player) {
		String name = player.getName();
		Credit creditClass = database.find(Credit.class).where().ieq("player_name", name).findUnique();
		if (creditClass == null) {
			creditClass = new Credit();
			creditClass.setPlayer(player);
			creditClass.setCredits(plugin.getConfig().getInt("credits", 1));

			database.save(creditClass);
		}

		return creditClass;
	}

	private List<InventoryItem> getInventoryItems(World world, Player player) {
		String id = world.getUID().toString();
		String playerName = player.getName();
		List<InventoryItem> inventoryItems = database.find(InventoryItem.class).where().ieq("worldUuid", id).ieq("player", playerName).findList();
		if (inventoryItems == null) {
			inventoryItems = new ArrayList<InventoryItem>();
		}

		return inventoryItems;
	}

	public enum CommandType {
		TORCH,
		OHNOEZ;
	}
}
