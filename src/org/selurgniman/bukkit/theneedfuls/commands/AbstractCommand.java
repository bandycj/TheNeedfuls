/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 22,
 *         2011
 */
public abstract class AbstractCommand implements CommandExecutor {

	private ISubCommand[] subCommands = null;
	private final TheNeedfuls plugin;

	public AbstractCommand(TheNeedfuls plugin) {
		this.plugin = plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender
	 * , org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (subCommands == null) {
			return false;
		}
		ISubCommand operation = null;

		String commandPermission = command.getPermission().replace(".*", "");
		if (args.length < 1) {
			if (!sender.hasPermission(command.getPermission())) {
				sender.sendMessage(Message.LACK_PERMISSION_MESSAGE.with(commandPermission));
				return true;
			}
		} else {
			try {
				for (ISubCommand subCommand : subCommands) {
					if (subCommand.toString().equals(args[0].toUpperCase())) {
						operation = subCommand;
						commandPermission += "." + subCommand.toString().toLowerCase();
						break;
					}
				}
			} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ex) {
			}

			if (args.length > 1) {
				commandPermission += "." + args[1].toLowerCase();
			}

			if (!sender.hasPermission(commandPermission)) {
				sender.sendMessage(Message.LACK_PERMISSION_MESSAGE.with(commandPermission));
				return true;
			}
		}

		TheNeedfuls.debug(commandPermission + ":" + sender.hasPermission(commandPermission));

		return processCommand(sender, command, label, args, operation);
	}

	abstract public boolean processCommand(CommandSender sender, Command command, String label, String[] args, ISubCommand operation);

	public void setSubCommands(ISubCommand[] subCommands) {
		this.subCommands = subCommands;
	}

	public String getWorldsList(String configKey) {
		List<World> worlds = new ArrayList<World>();
		for (String worldId : plugin.getConfig().getStringList(configKey)) {
			worlds.add(plugin.getServer().getWorld(UUID.fromString(worldId)));
		}
		return getWorldsList(worlds);
	}

	public String getWorldsList(List<World> worlds) {
		List<String> worldNames = new ArrayList<String>();
		for (World world : worlds) {
			if (world != null) {
				switch (world.getEnvironment()) {
					case NORMAL: {
						worldNames.add(ChatColor.GREEN + world.getName() + ":" + world.getUID() + ChatColor.WHITE);
						break;
					}
					case NETHER: {
						worldNames.add(ChatColor.RED + world.getName() + ":" + world.getUID() + ChatColor.WHITE);
						break;
					}
					case THE_END: {
						worldNames.add(ChatColor.DARK_BLUE + world.getName() + ":" + world.getUID() + ChatColor.WHITE);
						break;
					}
				}
			}
		}
		return worldNames.toString().replace("[", "").replace("]", "");
	}

	public void addRemoveWorld(String option, String worldId, String worldsKey) {
		List<String> worldIds = plugin.getConfig().getStringList(worldsKey);
		World world = plugin.getServer().getWorld(worldId);
		int worldIndex = Collections.binarySearch(worldIds, world.getUID().toString());

		if (option.equalsIgnoreCase("add") && worldIndex < 0) {
			worldIds.add(world.getUID().toString());
			Collections.sort(worldIds);
		} else if (option.equalsIgnoreCase("remove") && worldIndex > -1) {
			worldIds.remove(worldIndex);
		}

		plugin.getConfig().set(worldsKey, worldIds);
		plugin.saveConfig();
		plugin.getModel().loadWorldControls();
	}

	public TheNeedfuls getPlugin() {
		return this.plugin;
	}
}
