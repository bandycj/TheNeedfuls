/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.selurgniman.bukkit.theneedfuls.commands.HelpCommand;
import org.selurgniman.bukkit.theneedfuls.commands.IceCommand;
import org.selurgniman.bukkit.theneedfuls.commands.OhNoezCommand;
import org.selurgniman.bukkit.theneedfuls.commands.SheepCommand;
import org.selurgniman.bukkit.theneedfuls.commands.SortCommand;
import org.selurgniman.bukkit.theneedfuls.commands.TorchCommand;
import org.selurgniman.bukkit.theneedfuls.commands.WorldsCommand;
import org.selurgniman.bukkit.theneedfuls.commands.XpCommand;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.listeners.TheNeedfulsBlockListener;
import org.selurgniman.bukkit.theneedfuls.listeners.TheNeedfulsEntityListener;
import org.selurgniman.bukkit.theneedfuls.listeners.TheNeedfulsInventoryListener;
import org.selurgniman.bukkit.theneedfuls.listeners.TheNeedfulsPlayerListener;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;
import org.selurgniman.bukkit.theneedfuls.model.TorchModel;
import org.selurgniman.bukkit.theneedfuls.model.dao.Credit;
import org.selurgniman.bukkit.theneedfuls.model.dao.Torch;
import org.selurgniman.bukkit.theneedfuls.recipes.GlowstoneRecipe;

import com.avaje.ebean.EbeanServer;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class TheNeedfuls extends JavaPlugin {
	private final Logger log = Logger.getLogger("Minecraft");
	public static final BlockFace[] BLOCKFACES = new BlockFace[] { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.EAST, BlockFace.WEST,
			BlockFace.SOUTH, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST };
	private static final HashMap<String, Object> CONFIG_DEFAULTS = new HashMap<String, Object>();
	private Model model = null;
	private TheNeedfuls plugin = null;
	private int torchTaskId = -1;
	private int sheepTaskId = -1;

	static {
		CONFIG_DEFAULTS.put(SortCommand.CONFIG_SORT_DISTANCE, 5);
		CONFIG_DEFAULTS.put(IceCommand.CONFIG_ICE_QUANTITY, 64);
		CONFIG_DEFAULTS.put(SheepCommand.CONFIG_SHEEP_REFRESH, 60);
		CONFIG_DEFAULTS.put(WorldsCommand.CONFIG_TELEPORT_DELAY, 300);
		CONFIG_DEFAULTS.put(Credit.OHNOEZ_WORLDS_KEY, Collections.EMPTY_LIST);
		CONFIG_DEFAULTS.put(Torch.TORCH_REFRESH_KEY, 30);
		CONFIG_DEFAULTS.put(Torch.TORCH_AGE_KEY, 300);
		CONFIG_DEFAULTS.put(Torch.TORCH_WORLDS_KEY, Collections.EMPTY_LIST);
		for (Entry<String, Message> entry : Message.values()) {
			CONFIG_DEFAULTS.put(entry.getKey(), entry.getValue().toString());
		}
	}

	@Override
	public void onDisable() {
		this.saveConfig();
		log.info(Message.PREFIX + " disabled.");
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
		initSheepTask();

		saveConfig();
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(Message.PREFIX + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
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
		log.info(Message.PREFIX + " loaded " + ChatColor.GREEN + counter + ChatColor.WHITE + " worlds.");
	}

	private void initConfig() {
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}
		for (Entry<String, Object> entry : CONFIG_DEFAULTS.entrySet()) {
			if (this.getConfig().get(entry.getKey()) == null) {
				this.getConfig().set(entry.getKey(), entry.getValue());
			}
		}

		Message.setConfig(this.getConfig());
		log.info(Message.PREFIX + " config initialized.");
	}

	private void setupDatabase() {
		try {
			for (Class<?> dbClass : model.getDaoClasses()) {
				model.getDatabase().find(dbClass).findRowCount();
			}
		} catch (PersistenceException ex) {
			log.info("Installing database for " + getDescription().getName() + " due to first time usage");
			installDDL();
		}
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		TheNeedfulsEntityListener entityListener = new TheNeedfulsEntityListener(model);
		TheNeedfulsBlockListener blockListener = new TheNeedfulsBlockListener(this);
		TheNeedfulsPlayerListener playerListener = new TheNeedfulsPlayerListener(this);
		TheNeedfulsInventoryListener inventoryListener = new TheNeedfulsInventoryListener(this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_DISPENSE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.ENTITY_INTERACT, entityListener, Priority.Low, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, inventoryListener, Priority.Normal, this);
	}

	private void setCommandExecutors() {
		this.getCommand("tnh").setExecutor(new HelpCommand(this));
		this.getCommand("tnt").setExecutor(new TorchCommand(this));
		this.getCommand("tni").setExecutor(new IceCommand(this));
		this.getCommand("tnx").setExecutor(new XpCommand(this));
		this.getCommand("tns").setExecutor(new SheepCommand(this));
		this.getCommand("tnw").setExecutor(new WorldsCommand(this));
		this.getCommand("ohnoez").setExecutor(new OhNoezCommand(this));
		this.getCommand("sort").setExecutor(new SortCommand(this));

		log.info(Message.PREFIX + " set command executors.");
	}

	private void addRecipes() {
		this.getServer().addRecipe(new GlowstoneRecipe());
		log.info(Message.PREFIX + " loaded recipes.");
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
		log.info(Message.PREFIX + " torch expiration task started with id " + torchTaskId + ".");
	}

	/**
	 * Workaround for minecraft bug that causes sheep not to regrow wool.
	 */
	public void initSheepTask() {
		if (-1 != sheepTaskId) {
			this.getServer().getScheduler().cancelTask(sheepTaskId);
		}
		sheepTaskId = this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (World world : plugin.getServer().getWorlds()) {
					if (world.getEnvironment() == Environment.NORMAL) {
						for (LivingEntity livingEntity : world.getLivingEntities()) {
							if (livingEntity instanceof Sheep) {
								Sheep sheep = (Sheep) livingEntity;
								if (sheep.isSheared()) {
									Random r = new Random();
									sheep.setSheared(r.nextInt(1000) % 3 == 0);
								}
							}
						}
					}
				}
			}
		}, 60L, this.getConfig().getLong("sheep.refresh") * 20);
		log.info(Message.PREFIX + " sheep unshearing task started with id " + sheepTaskId + ".");
	}

	public Model getModel() {
		return this.model;
	}

	@Override
	public EbeanServer getDatabase() {
		return model.getDatabase();
	}
}
