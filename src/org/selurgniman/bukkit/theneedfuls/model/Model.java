/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.model.dao.Credit;
import org.selurgniman.bukkit.theneedfuls.model.dao.Drop;
import org.selurgniman.bukkit.theneedfuls.model.dao.Enchant;
import org.selurgniman.bukkit.theneedfuls.model.dao.InventoryEnchant;
import org.selurgniman.bukkit.theneedfuls.model.dao.InventoryItem;
import org.selurgniman.bukkit.theneedfuls.model.dao.Torch;

import com.avaje.ebean.EbeanServer;
import com.lennardf1989.bukkitex.MyDatabase;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 14, 2011
 */
public class Model {
	private final MyDatabase myDatabase;
	private final TheNeedfuls plugin;

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
	
	public Model(TheNeedfuls plugin) {
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
				return getDaoClasses();
			};
		};
		this.myDatabase.initializeDatabase(plugin.getConfig().getString(DATABASE_DRIVER), plugin.getConfig().getString(DATABASE_URL), plugin
				.getConfig()
				.getString(DATABASE_USER_NAME), plugin.getConfig().getString(DATABASE_PASSWORD), plugin.getConfig().getString(DATABASE_ISOLATION), plugin
				.getConfig()
				.getBoolean(DATABASE_LOGGING), plugin.getConfig().getBoolean(DATABASE_REBUILD));
		plugin.getConfig().set(DATABASE_REBUILD, false);
		plugin.saveConfig();

		loadWorldControls();
		for (CommandType commandType:CommandType.values()){
			commandType.getModel().setPlugin(plugin);
		}
	}

	public void loadWorldControls() {
		commandWorlds.clear();
		for (CommandType command : CommandType.values()) {
			commandWorlds.put(command, new HashSet<World>());
		}
		addCommandWorlds(CommandType.TORCH, Torch.TORCH_WORLDS_KEY);
		addCommandWorlds(CommandType.OHNOEZ, Credit.OHNOEZ_WORLDS_KEY);
	}
	
	public List<Class<?>> getDaoClasses(){
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(Torch.class);
		list.add(Credit.class);
		list.add(Drop.class);
		list.add(Enchant.class);
		list.add(InventoryItem.class);
		list.add(InventoryEnchant.class);
		return list;
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
	
	public static AbstractCommandModel getCommandModel(CommandType type){
		return type.getModel();
	}

	public enum CommandType {
		TORCH(new TorchModel()),
		OHNOEZ(new OhNoezModel()),
		WORLDS(new WorldsModel());
		
		private final AbstractCommandModel commandModel;
		private CommandType(AbstractCommandModel commandModel){
			this.commandModel = commandModel;
		}
				
		public AbstractCommandModel getModel(){
			return this.commandModel;
		}
	}
}
