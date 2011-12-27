/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class WorldsCommand extends AbstractCommand {
	private final TheNeedfuls plugin;
	public final static String CONFIG_TELEPORT_DELAY = "worlds.teleportDelaySeconds";

	/**
	 * @param name
	 * @param plugin
	 */
	public WorldsCommand(TheNeedfuls plugin) {
		super(plugin);
		this.plugin = plugin;
		this.setSubCommands(WorldsSubCommand.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender
	 * , org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean processCommand(CommandSender sender, Command command, String label, String[] args, ISubCommand operation, String option) {
		if (operation instanceof WorldsSubCommand) {
			switch ((WorldsSubCommand) operation) {
				case CREATE: {
					if (!option.isEmpty()) {
						WorldCreator creator = new WorldCreator(option);
						plugin.getServer().createWorld(creator);

						sender.sendMessage(String.format(Message.WORLD_CREATED_MESSAGE.toString(), option));
						return true;
					}

					break;
				}
				case DELETE: {
					Server server = plugin.getServer();
					try {
						World world = server.getWorld(UUID.fromString(option));
						String name = world.getName();
						server.unloadWorld(world, false);
						server.getWorld(option).getWorldFolder().delete();

						sender.sendMessage(String.format(Message.WORLD_DELETED_MESSAGE.toString(), name));

						return true;
					} catch (IllegalArgumentException ex) {
						sender.sendMessage(ex.getMessage());
					}

					break;
				}
				case LIST: {
					sender.sendMessage(String.format(Message.WORLD_LIST_MESSAGE.toString(), getWorldsList(plugin.getServer().getWorlds())));

					return true;
				}
				case PORT: {
					if (sender instanceof Player && !option.isEmpty()) {
						World world = plugin.getServer().getWorld(option);
						Player player = (Player) sender;
						player.teleport(world.getSpawnLocation(), TeleportCause.COMMAND);

						return true;
					}

					break;
				}
				case DELAY: {
					try {
						plugin.getConfig().set(WorldsCommand.CONFIG_TELEPORT_DELAY, Integer.parseInt(option));
						plugin.saveConfig();
					} catch (NumberFormatException ex) {

					}

					sender.sendMessage(String.format(Message.WORLD_DELAY_MESSAGE.toString(), plugin.getConfig().get(WorldsCommand.CONFIG_TELEPORT_DELAY)));

					return true;
				}
			}
		}

		return false;
	}

	public static enum WorldsSubCommand
			implements
			ISubCommand {
		CREATE(
				ChatColor.GREEN + "World Create: " + ChatColor.WHITE + "creates a new world.",
				"/tnw create <world name>"),
		DELETE(
				ChatColor.GREEN + "World Delete: " + ChatColor.WHITE + "deletes a world.",
				"/tnw delete <world uuid>"),
		LIST(
				ChatColor.GREEN + "World Delete: " + ChatColor.WHITE + "lists the loaded worlds.",
				"/tnw list"),
		PORT(
				ChatColor.GREEN + "World Port: " + ChatColor.WHITE + "teleports you to a world.",
				"/tnw port <world name>"),
		DELAY(
				ChatColor.GREEN + "World Delay: " + ChatColor.WHITE + "displays and sets the delay between use of teleporters.",
				"/tnw delay <seconds>");

		private final String help;
		private final String usage;

		private WorldsSubCommand(String help, String usage) {
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
