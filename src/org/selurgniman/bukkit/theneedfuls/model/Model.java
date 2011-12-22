/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.selurgniman.bukkit.theneedfuls.Message;

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

	private final HashSet<World> torchWorlds = new HashSet<World>();

	public Model(JavaPlugin plugin) {
		this.plugin = plugin;
		CONFIG_DEFAULTS.put(DATABASE_URL, "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "\\" + plugin.getDescription().getName() + ".db");

		for (Entry<String, Object> entry : CONFIG_DEFAULTS.entrySet()) {
			if (getConfig().get(entry.getKey()) == null) {
				getConfig().set(entry.getKey(), entry.getValue());
			}
		}

		this.myDatabase = new MyDatabase(plugin) {
			@Override
			protected java.util.List<Class<?>> getDatabaseClasses() {
				List<Class<?>> list = new ArrayList<Class<?>>();
				list.add(Torch.class);
				list.add(Credit.class);
				list.add(Drop.class);
				return list;
			};
		};
		this.myDatabase.initializeDatabase(
				getConfig().getString(DATABASE_DRIVER),
				getConfig().getString(DATABASE_URL),
				getConfig().getString(DATABASE_USER_NAME),
				getConfig().getString(DATABASE_PASSWORD),
				getConfig().getString(DATABASE_ISOLATION),
				getConfig().getBoolean(DATABASE_LOGGING),
				getConfig().getBoolean(DATABASE_REBUILD));
		getConfig().set(DATABASE_REBUILD, false);
		this.plugin.saveConfig();

		this.database = myDatabase.getDatabase();

		loadWorldControls();
	}

	public void loadWorldControls() {
		torchWorlds.clear();
		for (String worldId : plugin.getConfig().getStringList(Torch.TORCH_WORLDS_KEY)) {
			torchWorlds.add(plugin.getServer().getWorld(UUID.fromString(worldId)));
		}
	}

	public boolean isTorchWorld(World world) {
		return torchWorlds.contains(world);
	}

	public EbeanServer getDatabase() {
		return this.myDatabase.getDatabase();
	}

	public FileConfiguration getConfig() {
		return this.plugin.getConfig();
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
				creditClass.setCredits(Integer.parseInt(plugin.getConfig().getString("credits", "1")));
				database.save(creditClass);
			}
		}

		return creditClass.getCredits();
	}
	public int getLastExperience(Player player){
		Credit creditClass = getRecordForPlayer(player);
		for (Drop drop : creditClass.getDrops()) {
			if (drop.getItemId() == -2) {
				return drop.getItemCount();
			} 
		}
		
		return 0;
	}
	public List<ItemStack> getLastInventory(Player player) {
		Credit creditClass = getRecordForPlayer(player);
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (Drop drop : creditClass.getDrops()) {
			if (drop.getItemId() != -2) {
				items.add(new ItemStack(Material.getMaterial(drop.getItemId()), drop.getItemCount(),drop.getItemDurability().shortValue(),drop.getItemData().byteValue()));
			} 
		}
		return items;
	}

	public void addAvailableCredits(Player player, int count) {
		setAvailableCredits(player, getAvailableCredits(player) + count);
	}

	public void setAvailableCredits(Player player, int count) {
		Credit creditClass = getRecordForPlayer(player);
		creditClass.setCredits(count);

		database.save(creditClass);
	}

	public void setLastInventory(Player player, List<ItemStack> itemsList, Integer droppedExp) {
		Credit creditClass = getRecordForPlayer(player);
		ArrayList<Drop> drops = new ArrayList<Drop>();
		Drop drop = new Drop();
		drop.setItemId(-2);
		System.out.println("level: "+player.getLevel()+":"+droppedExp);
		drop.setItemCount(player.getLevel());
		drop.setItemData(0);
		drop.setItemDurability(0);
		drop.setCredit(creditClass);
		drops.add(drop);
		
		for (ItemStack item : itemsList) {
			Byte data = item.getData().getData();
			drop = new Drop();
			drop.setItemId(item.getTypeId());
			drop.setItemCount(item.getAmount());
			drop.setItemData(data.intValue());
			drop.setItemDurability(new Integer(item.getDurability()));
			drop.setCredit(creditClass);
			drops.add(drop);
		}

		database.save(drops);
	}

	private synchronized void removeTorch(Torch torch) {
		database.delete(torch);
	}

	private synchronized Torch createTorch(Block torch) {
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
		Credit creditClass = database.find(Credit.class).where().ieq("playerName", name).findUnique();
		if (creditClass == null) {
			creditClass = new Credit();
			creditClass.setPlayer(player);
			creditClass.setCredits(plugin.getConfig().getInt("credits", 1));
			
			database.save(creditClass);
		}

		return creditClass;
	}
}
