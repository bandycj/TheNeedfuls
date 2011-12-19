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
import org.selurgniman.bukkit.theneedfuls.Message;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.model.Torch;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class TorchCommand implements CommandExecutor {
	private final TheNeedfuls plugin;

	/**
	 * @param name
	 * @param plugin
	 */
	public TorchCommand(TheNeedfuls plugin) {
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
		TorchSubCommand operation = null;
		try {
			operation = TorchSubCommand.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException ex) {
			return false;
		} catch (ArrayIndexOutOfBoundsException ex) {
			return false;
		}

		String option = "";
		if (args.length > 1) {
			option = args[1];
		}

		if (option.equalsIgnoreCase("help")) {
			sender.sendMessage(operation.getHelp());
			sender.sendMessage(operation.getUsage());

			return true;
		}

		if (!sender.hasPermission(command.getPermission() + "." + operation.toString().toLowerCase())) {
			sender.sendMessage("You are not an op or lack the permission: " + command.getPermission() + "." + operation.toString().toLowerCase());
			return false;
		}

		switch (operation) {
			case AGE: {
				try {
					plugin.getConfig().set(Torch.TORCH_AGE_KEY, Integer.parseInt(option));
					plugin.saveConfig();
					plugin.initTorchTask();
				} catch (NumberFormatException ex) {

				}
				
				sender.sendMessage(String.format(Message.TORCH_AGE_MESSAGE.toString(), plugin.getConfig().get(Torch.TORCH_AGE_KEY)));
				return true;
			}
			case REFRESH: {
				try {
					plugin.getConfig().set(Torch.TORCH_REFRESH_KEY, Integer.parseInt(option));
					plugin.saveConfig();
					plugin.initTorchTask();
				} catch (NumberFormatException ex) {

				}
				
				sender.sendMessage(String.format(Message.TORCH_REFRESH_MESSAGE.toString(), plugin.getConfig().get(Torch.TORCH_REFRESH_KEY)));
				return true;
			}
			case COUNT: {
				int torchCount = plugin.getModel().getTorchCount();
				sender.sendMessage(String.format(Message.TORCH_COUNT_MESSAGE.toString(), torchCount));

				return true;
			}
			case EXPIRE: {
				int torchCount = plugin.getModel().getTorchCount();

				plugin.getModel().expireAllTorches();
				sender.sendMessage(String.format(Message.TORCH_EXPIRE_MESSAGE.toString(), torchCount));
				torchCount = plugin.getModel().getTorchCount();
				sender.sendMessage(String.format(Message.TORCH_COUNT_MESSAGE.toString(), torchCount));

				return true;
			}
			case WORLDS: {
				if (args.length > 2) {
					List<String> worldIds = plugin.getConfig().getStringList(Torch.TORCH_WORLDS_KEY);
					World world = plugin.getServer().getWorld(args[2]);
					int worldIndex = Collections.binarySearch(worldIds, world.getUID().toString());

					if (option.equalsIgnoreCase("add") && worldIndex < 0) {
						worldIds.add(world.getUID().toString());
						Collections.sort(worldIds);
					} else if (option.equalsIgnoreCase("remove") && worldIndex > -1) {
						worldIds.remove(worldIndex);
					}

					plugin.getConfig().set(Torch.TORCH_WORLDS_KEY, worldIds);
					plugin.saveConfig();
				}

				List<String> worlds = new ArrayList<String>();
				for (String worldId : plugin.getConfig().getStringList(Torch.TORCH_WORLDS_KEY)) {
					World world = plugin.getServer().getWorld(UUID.fromString(worldId));
					if (world != null) {
						switch (world.getEnvironment()) {
							case NORMAL: {
								worlds.add(ChatColor.GREEN + world.getName() + ChatColor.WHITE);
								break;
							}
							case NETHER: {
								worlds.add(ChatColor.RED + world.getName() + ChatColor.WHITE);
								break;
							}
							case THE_END: {
								worlds.add(ChatColor.DARK_BLUE + world.getName() + ChatColor.WHITE);
								break;
							}
						}
					}
				}

				sender.sendMessage(String.format(Message.TORCH_WORLDS_MESSAGE.toString(), worlds).replace("[", "").replace("]", ""));
				return true;
			}

			default: {
				return false;
			}
		}
	}

	public enum TorchSubCommand {
		AGE(
				ChatColor.GREEN + "Torch Age: " + ChatColor.WHITE + "displays and sets the maximum age of torches in seconds.",
				"/tnt age <number of seconds>"),
		REFRESH(
				ChatColor.GREEN + "Torch Refresh: " + ChatColor.WHITE + "displays and sets the frequency to check for expired torches.",
				"/tnt refresh"),
		COUNT(
				ChatColor.GREEN + "Torch Count: " + ChatColor.WHITE + "displays the total count of torches being tracked.",
				"/tnt count"),
		EXPIRE(
				ChatColor.GREEN + "Torch Expire: " + ChatColor.WHITE + "exires all torches being tracked.",
				"/tnt expire"),
		WORLDS(
				ChatColor.GREEN + "Torch Worlds: " + ChatColor.WHITE + "sets the worlds to track torches for.",
				"/tnt worlds <add|remove> <world name>");

		private final String help;
		private final String usage;

		private TorchSubCommand(String help, String usage) {
			this.help = help;
			this.usage = usage;
		}

		public String getHelp() {
			return this.help;
		}

		public String getUsage() {
			return this.usage;
		}
	}
}
