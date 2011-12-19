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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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
}
