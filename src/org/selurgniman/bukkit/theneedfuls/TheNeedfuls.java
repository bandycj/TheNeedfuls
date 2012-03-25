/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.selurgniman.bukkit.theneedfuls.commands.HelpCommand;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;
import org.selurgniman.bukkit.theneedfuls.model.dao.Credit;
import org.selurgniman.bukkit.theneedfuls.model.dao.Torch;
import org.selurgniman.bukkit.theneedfuls.parts.enchantingrepair.EnchantingRepairListener;
import org.selurgniman.bukkit.theneedfuls.parts.ice.IceCommand;
import org.selurgniman.bukkit.theneedfuls.parts.ice.IceListener;
import org.selurgniman.bukkit.theneedfuls.parts.misc.DebugCommand;
import org.selurgniman.bukkit.theneedfuls.parts.misc.DropEnhancerListener;
import org.selurgniman.bukkit.theneedfuls.parts.misc.SignPlacerListener;
import org.selurgniman.bukkit.theneedfuls.parts.misc.SortCommand;
import org.selurgniman.bukkit.theneedfuls.parts.misc.XpCommand;
import org.selurgniman.bukkit.theneedfuls.parts.ohnoez.OhNoezCommand;
import org.selurgniman.bukkit.theneedfuls.parts.ohnoez.OhNoezListener;
import org.selurgniman.bukkit.theneedfuls.parts.torch.TorchCommand;
import org.selurgniman.bukkit.theneedfuls.parts.torch.TorchListener;
import org.selurgniman.bukkit.theneedfuls.parts.torch.TorchModel;
import org.selurgniman.bukkit.theneedfuls.parts.weather.WeatherListener;
import org.selurgniman.bukkit.theneedfuls.parts.worlds.WorldsCommand;
import org.selurgniman.bukkit.theneedfuls.parts.worlds.WorldsListener;
import org.selurgniman.bukkit.theneedfuls.recipes.GlowstoneRecipe;
import org.selurgniman.bukkit.theneedfuls.recipes.NetherBrickRecipe;

import com.avaje.ebean.EbeanServer;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class TheNeedfuls extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");
	public static final BlockFace[] BLOCKFACES = new BlockFace[] {
			BlockFace.NORTH,
			BlockFace.NORTH_EAST,
			BlockFace.NORTH_WEST,
			BlockFace.EAST,
			BlockFace.WEST,
			BlockFace.SOUTH,
			BlockFace.SOUTH_EAST,
			BlockFace.SOUTH_WEST
	};
	private static final LinkedHashMap<String, Object> CONFIG_DEFAULTS = new LinkedHashMap<String, Object>();
	private Model model = null;
	private TheNeedfuls plugin = null;
	private int torchTaskId = -1;
	private static boolean isDebug = false;

	static {
		// CONFIG_DEFAULTS.put(AfkCommand.CONFIG_AFK_DELAY, 300);
		CONFIG_DEFAULTS.put(SortCommand.CONFIG_SORT_DISTANCE, 5);
		CONFIG_DEFAULTS.put(IceCommand.CONFIG_ICE_QUANTITY, 64);
		CONFIG_DEFAULTS.put(WorldsCommand.CONFIG_TELEPORT_DELAY, 300);
		CONFIG_DEFAULTS.put(Credit.OHNOEZ_WORLDS_KEY, Collections.EMPTY_LIST);
		CONFIG_DEFAULTS.put(Torch.TORCH_REFRESH_KEY, 30);
		CONFIG_DEFAULTS.put(Torch.TORCH_AGE_KEY, 300);
		CONFIG_DEFAULTS.put(Torch.TORCH_WORLDS_KEY, Collections.EMPTY_LIST);
		CONFIG_DEFAULTS.put("Debug", false);
		CONFIG_DEFAULTS.put("DebugPlayer", false);
		for (Entry<String, Message> entry : Message.values()) {
			CONFIG_DEFAULTS.put(entry.getKey(), entry.getValue().toString());
		}
	}

	@Override
	public void onDisable() {
		this.saveConfig();
		log("disabled.");
	}

	@Override
	public void onEnable() {
		this.plugin = this;

		loadWorlds();
		initConfig();

		this.model = new Model(this);
		setupDatabase();

		registerEvents();
		setCommandExecutors();

		addRecipes();
		initTorchTask();

		saveConfig();
		PluginDescriptionFile pdfFile = this.getDescription();
		log(pdfFile.getName() + "version " + pdfFile.getVersion() + "is enabled!");
	}

	private void loadWorlds() {
		String[] worlds = this.plugin.getServer().getWorldContainer().list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				File subDir = new File(dir, name);
				if (subDir.isDirectory()) {
					for (File file : subDir.listFiles()) {
						if (file.getName().equalsIgnoreCase("level.dat")) {
							return true;
						}
					}
				}

				return false;
			}
		});
		int counter = 0;
		for (String world : worlds) {
			if (this.getServer().getWorld(world) == null) {
				WorldCreator creator = new WorldCreator(world);
				this.getServer().createWorld(creator);
				counter++;
			}
		}
		log("loaded " + ChatColor.GREEN + counter + ChatColor.WHITE + "worlds.");
	}

	private void initConfig() {
		getConfig();
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}
		for (Entry<String, Object> entry : CONFIG_DEFAULTS.entrySet()) {
			if (this.getConfig().get(entry.getKey()) == null) {
				this.getConfig().set(entry.getKey(), entry.getValue());
			}
		}

		Message.setConfig(this.getConfig());

		log("config initialized.");
	}
	
	@Override
	public void reloadConfig(){
		super.reloadConfig();
		TheNeedfuls.isDebug = this.getConfig().getBoolean("Debug");
	}

	private void setupDatabase() {
		try {
			for (Class<?> dbClass : model.getDaoClasses()) {
				model.getDatabase().find(dbClass).findRowCount();
			}
		} catch (PersistenceException ex) {
			log("Installing database for " + getDescription().getName() + "due to first time usage.");
			installDDL();
		}
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		// pm.registerEvent(new AfkPlayerListener(this);, this);
		pm.registerEvents(new EnchantingRepairListener(), this);
		pm.registerEvents(new OhNoezListener(model), this);
		pm.registerEvents(new TorchListener(model), this);
		pm.registerEvents(new IceListener(model), this);
		pm.registerEvents(new DropEnhancerListener(), this);
		pm.registerEvents(new WorldsListener(this), this);
		pm.registerEvents(new WeatherListener(), this);
		pm.registerEvents(new SignPlacerListener(), this);
	}

	private void setCommandExecutors() {
		this.getCommand("tnh").setExecutor(new HelpCommand(this));
		this.getCommand("tnt").setExecutor(new TorchCommand(this));
		this.getCommand("tni").setExecutor(new IceCommand(this));
		this.getCommand("tnx").setExecutor(new XpCommand(this));
		this.getCommand("tnw").setExecutor(new WorldsCommand(this));
		this.getCommand("ohnoez").setExecutor(new OhNoezCommand(this));
		this.getCommand("sort").setExecutor(new SortCommand(this));
		this.getCommand("tnd").setExecutor(new DebugCommand(this));
		// this.getCommand("afk").setExecutor(new AfkCommand(this));

		log("set command executors.");
	}

	private void addRecipes() {
		this.getServer().addRecipe(new GlowstoneRecipe());
		this.getServer().addRecipe(new NetherBrickRecipe());
		log("loaded recipes.");
	}

	private boolean isDebug() {
		return this.getConfig().getBoolean("Debug");
	}

	public void initTorchTask() {
		if (-1 != torchTaskId) {
			this.getServer().getScheduler().cancelTask(torchTaskId);
		}

		torchTaskId = this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				((TorchModel) Model.getCommandModel(CommandType.TORCH)).expireTorches();
			}
		}, 60L, this.getConfig().getLong(Torch.TORCH_REFRESH_KEY) * 20);
		log("torch expiration task started with id " + torchTaskId + ".");
	}

	public Model getModel() {
		return this.model;
	}

	@Override
	public EbeanServer getDatabase() {
		return model.getDatabase();
	}

	public static void log(String message) {
		log.info(Message.PREFIX + " " + message);
	}

	public static void debug(String message) {
		if (isDebug) {
			log("DEBUG: " + message);
		}
	}
}
