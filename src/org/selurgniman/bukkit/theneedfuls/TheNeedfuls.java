/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.selurgniman.bukkit.theneedfuls.commands.HelpCommand;
import org.selurgniman.bukkit.theneedfuls.commands.IceCommand;
import org.selurgniman.bukkit.theneedfuls.commands.OhNoezCommand;
import org.selurgniman.bukkit.theneedfuls.commands.TorchCommand;
import org.selurgniman.bukkit.theneedfuls.commands.XpCommand;
import org.selurgniman.bukkit.theneedfuls.listeners.TheNeedfulsBlockListener;
import org.selurgniman.bukkit.theneedfuls.listeners.TheNeedfulsEntityListener;
import org.selurgniman.bukkit.theneedfuls.model.Credit;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Torch;

import com.avaje.ebean.EbeanServer;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class TheNeedfuls extends JavaPlugin {
	private final Logger log = Logger.getLogger("Minecraft");
	private static final HashMap<String, Object> CONFIG_DEFAULTS = new HashMap<String, Object>();
	private Model model = null;
	private int torchTaskId = -1;

	static {
		CONFIG_DEFAULTS.put("iceMaker.quantity", 64);
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
		try {
			this.getDataFolder().createNewFile();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage());
		}
		for (Entry<String,Object>entry:CONFIG_DEFAULTS.entrySet()){
			if(this.getConfig().get(entry.getKey()) == null){
				this.getConfig().set(entry.getKey(), entry.getValue());
			}
		}
		
		Message.setConfig(this.getConfig());

		this.model = new Model(this);
		setupDatabase();

		PluginManager pm = getServer().getPluginManager();
		TheNeedfulsEntityListener entityLlistener = new TheNeedfulsEntityListener(model);
		TheNeedfulsBlockListener blockLlistener = new TheNeedfulsBlockListener(this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockLlistener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockLlistener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_DISPENSE, blockLlistener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityLlistener, Priority.Highest, this);
		
		initTorchTask();

		this.getCommand("tnh").setExecutor(new HelpCommand(this));
		this.getCommand("tnt").setExecutor(new TorchCommand(this));
		this.getCommand("tni").setExecutor(new IceCommand(this));
		this.getCommand("tnx").setExecutor(new XpCommand(this));
		this.getCommand("ohnoez").setExecutor(new OhNoezCommand(this));

		saveConfig();
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(Message.PREFIX + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	private void setupDatabase() {
		try {
			model.getDatabase().find(Torch.class).findRowCount();

		} catch (PersistenceException ex) {
			log.info("Installing database for " + getDescription().getName() + " due to first time usage");
			installDDL();
		}
	}

	public void initTorchTask() {
		if (-1 != torchTaskId) {
			this.getServer().getScheduler().cancelTask(torchTaskId);
		}

		torchTaskId = this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				model.expireTorches();
			}
		}, 60L, this.getConfig().getLong(Torch.TORCH_REFRESH_KEY) * 20);
	}

	public Model getModel() {
		return this.model;
	}

	@Override
	public EbeanServer getDatabase() {
		return model.getDatabase();
	}
}
