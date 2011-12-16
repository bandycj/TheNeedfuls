/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls;

import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 15,
 *         2011
 */
public class TheNeedfuls extends JavaPlugin {
	private final Logger log = Logger.getLogger("Minecraft");
	private static final MemoryConfiguration CONFIG_DEFAULTS = new MemoryConfiguration();
	private Model model = null;

	static {
		CONFIG_DEFAULTS.addDefault("torchAge", 120);
		for (Entry<String, Message> entry : Message.values()) {
			CONFIG_DEFAULTS.addDefault(entry.getKey(), entry.getValue().toString());
		}
	}

	@Override
	public void onDisable() {
		this.saveConfig();
		log.info(Message.PREFIX + " disabled.");
	}

	@Override
	public void onEnable() {
		this.getConfig().setDefaults(CONFIG_DEFAULTS);
		Message.setConfig(this.getConfig());

		this.model = new Model(this);
		setupDatabase();

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_PLACE, new BlockPlacedListener(model), Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, new PlayerListener() {
			@Override
			public void onPlayerInteract(PlayerInteractEvent event) {
				Block clickedBlock = event.getClickedBlock();
				if (event.getMaterial() == Material.WATER_BUCKET && clickedBlock.getType() == Material.IRON_BLOCK
						&& clickedBlock.getRelative(BlockFace.DOWN).getType() == Material.SNOW_BLOCK) {
					System.out.println(clickedBlock+":"+clickedBlock.getRelative(BlockFace.UP));
					event.getPlayer().getWorld().dropItemNaturally(clickedBlock.getRelative(BlockFace.UP).getLocation(), new ItemStack(Material.ICE,1));
					event.setCancelled(true);
				}
			}
		}, Priority.Highest, this);

		this.getCommand("theneedfuls").setExecutor(new TheneedfulsCommandExecutor(model));

		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(Message.PREFIX + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}

	private void setupDatabase() {
		try {
			model.getDatabase().find(Torch.class).findRowCount();

		} catch (PersistenceException ex) {
			System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
			installDDL();
		}
	}

	@Override
	public EbeanServer getDatabase() {
		return model.getDatabase();
	}
}
