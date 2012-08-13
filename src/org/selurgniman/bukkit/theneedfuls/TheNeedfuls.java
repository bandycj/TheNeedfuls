/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls;

import java.io.File;
import java.io.FilenameFilter;
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
	private Model model = null;
	private TheNeedfuls plugin = null;
	private int torchTaskId = -1;
	private static boolean isDebug = false;

	@Override
	public void onDisable() {
		this.saveConfig();
		log("disabled.");
	}

	@Override
	public void onEnable() {
		this.plugin = this;

		initConfig();
		if (this.getConfig().getBoolean(ConfigUtils.WORLDS_ENABLED)) {
			loadWorlds();
		}

		this.model = new Model(this);
		setupDatabase();

		registerEvents();
		setCommandExecutors();

		addRecipes();
		if (this.getConfig().getBoolean(ConfigUtils.TORCH_ENABLED)) {
			initTorchTask();
		}

		saveConfig();
		PluginDescriptionFile pdfFile = this.getDescription();
		log(pdfFile.getName() + "version " + pdfFile.getVersion() + "is enabled!");
	}

	private void loadWorlds() {
		if (plugin.getConfig().getBoolean(ConfigUtils.WORLDS_ENABLED)) {
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
	}

	private void initConfig() {
		getConfig();
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}
		for (Entry<String, Object> entry : ConfigUtils.getDefaults().entrySet()) {
			if (this.getConfig().get(entry.getKey()) == null) {
				this.getConfig().set(entry.getKey(), entry.getValue());
			}
		}

		Message.setConfig(this.getConfig());

		log("config initialized.");
	}

	@Override
	public void reloadConfig() {
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
		// pm.registerEvents(new CombinedInventoryListener(), this);
		if (this.getConfig().getBoolean(ConfigUtils.ENCHANTING_REPAIR_ENABLED)) {
			pm.registerEvents(new EnchantingRepairListener(), this);
		}
		if (this.getConfig().getBoolean(ConfigUtils.OHNOEZ_ENABLED)) {
			pm.registerEvents(new OhNoezListener(model), this);
		}
		if (this.getConfig().getBoolean(ConfigUtils.TORCH_ENABLED)) {
			pm.registerEvents(new TorchListener(model), this);
		}
		if (this.getConfig().getBoolean(ConfigUtils.ICE_ENABLED)) {
			pm.registerEvents(new IceListener(model), this);
		}
		if (this.getConfig().getBoolean(ConfigUtils.DROP_ENHANCER_ENABLED)) {
			pm.registerEvents(new DropEnhancerListener(), this);
		}
		if (this.getConfig().getBoolean(ConfigUtils.WORLDS_ENABLED)) {
			pm.registerEvents(new WorldsListener(this), this);
		}
		if (this.getConfig().getBoolean(ConfigUtils.WEATHER_ENABLED)) {
			pm.registerEvents(new WeatherListener(), this);
		}
		if (this.getConfig().getBoolean(ConfigUtils.SIGN_PLACER_ENABLED)) {
			pm.registerEvents(new SignPlacerListener(), this);
		}
		log("registered command listeners.");
	}

	private void setCommandExecutors() {
		// this.getCommand("afk").setExecutor(new AfkCommand(this));
		this.getCommand("tnh").setExecutor(new HelpCommand(this));
		if (this.getConfig().getBoolean(ConfigUtils.TORCH_ENABLED)) {
			this.getCommand("tnt").setExecutor(new TorchCommand(this));
		}
		if (this.getConfig().getBoolean(ConfigUtils.ICE_ENABLED)) {
			this.getCommand("tni").setExecutor(new IceCommand(this));
		}
		if (this.getConfig().getBoolean(ConfigUtils.XP_ENABLED)) {
			this.getCommand("tnx").setExecutor(new XpCommand(this));
		}
		if (this.getConfig().getBoolean(ConfigUtils.WORLDS_ENABLED)) {
			this.getCommand("tnw").setExecutor(new WorldsCommand(this));
		}
		if (this.getConfig().getBoolean(ConfigUtils.OHNOEZ_ENABLED)) {
			this.getCommand("ohnoez").setExecutor(new OhNoezCommand(this));
		}
		if (this.getConfig().getBoolean(ConfigUtils.SORT_ENABLED)) {
			this.getCommand("sort").setExecutor(new SortCommand(this));
		}
		if (this.getConfig().getBoolean(ConfigUtils.DEBUG_ENABLED)) {
			this.getCommand("tnd").setExecutor(new DebugCommand(this));
		}
		log("set command executors.");
	}

	private void addRecipes() {
		this.getServer().addRecipe(new GlowstoneRecipe());
		this.getServer().addRecipe(new NetherBrickRecipe());
		log("loaded recipes.");
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
