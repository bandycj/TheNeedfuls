/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.lennardf1989.bukkitex.MyDatabase;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 14,
 *         2011
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

	public Model(JavaPlugin plugin) {
		this.plugin = plugin;
		this.plugin.getConfig().addDefault(DATABASE_DRIVER, "org.sqlite.JDBC");
		this.plugin.getConfig().addDefault(DATABASE_URL,
				"jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "\\" + plugin.getDescription().getName() + ".db");
		this.plugin.getConfig().addDefault(DATABASE_USER_NAME, "bukkit");
		this.plugin.getConfig().addDefault(DATABASE_PASSWORD, "walrus");
		this.plugin.getConfig().addDefault(DATABASE_ISOLATION, "SERIALIZABLE");
		this.plugin.getConfig().addDefault(DATABASE_LOGGING, Boolean.FALSE);
		this.plugin.getConfig().addDefault(DATABASE_REBUILD, Boolean.TRUE);

		this.myDatabase = new MyDatabase(plugin) {
			@Override
			protected java.util.List<Class<?>> getDatabaseClasses() {
				List<Class<?>> list = new ArrayList<Class<?>>();
				list.add(Torch.class);
				return list;
			};
		};
		this.myDatabase.initializeDatabase(plugin.getConfig().getString(DATABASE_DRIVER), plugin.getConfig().getString(DATABASE_URL), plugin.getConfig()
				.getString(DATABASE_USER_NAME), plugin.getConfig().getString(DATABASE_PASSWORD), plugin.getConfig().getString(DATABASE_ISOLATION), plugin
				.getConfig().getBoolean(DATABASE_LOGGING, false), plugin.getConfig().getBoolean(DATABASE_REBUILD, true));
		this.plugin.getConfig().set("database.rebuild", false);
		this.plugin.saveConfig();

		this.database = myDatabase.getDatabase();
	}

	public EbeanServer getDatabase() {
		return this.myDatabase.getDatabase();
	}
	
	public boolean isTorchExpired(Block torch){
		Torch torchClass = getRecordForTorch(torch);
		Date placedDate = torchClass.getPlacedDate();
		int duration = (int) ((new Date().getTime() - placedDate.getTime()) / 1000);
		if (duration >= plugin.getConfig().getInt("torchAge", 120)) {
			database.delete(torchClass);
			return true;
		}
		return false;
	}
	
	public void addTorch(Block torch){
		getRecordForTorch(torch);
	}

	private Torch getRecordForTorch(Block torch) {
		Map<String, Object> location = new HashMap<String, Object>();
		location.put("blockX", String.valueOf(torch.getX()));
		location.put("blockY", String.valueOf(torch.getY()));
		location.put("blockZ", String.valueOf(torch.getZ()));

		Torch torchClass = database.find(Torch.class).where().allEq(location).findUnique();
		if (torchClass == null) {
			torchClass = new Torch();
			torchClass.setBlockX(torch.getX());
			torchClass.setBlockY(torch.getY());
			torchClass.setBlockZ(torch.getZ());
			torchClass.setPlacedDate(new Date());

			database.save(torchClass);
		}

		return torchClass;
	}
}
