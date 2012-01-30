/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.worlds;

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
import org.selurgniman.bukkit.theneedfuls.commands.AbstractCommand;
import org.selurgniman.bukkit.theneedfuls.commands.ISubCommand;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class WorldsCommand extends AbstractCommand {
	public final static String CONFIG_TELEPORT_DELAY = "worlds.teleportDelaySeconds";

	public final WorldsModel model;

	/**
	 * @param name
	 * @param getPlugin
	 *            ()
	 */
	public WorldsCommand(TheNeedfuls plugin) {
		super(plugin);
		this.model = (WorldsModel) Model.getCommandModel(CommandType.WORLDS);
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
	public boolean processCommand(CommandSender sender, Command command, String label, String[] args, ISubCommand operation) {
		if (operation != null && operation instanceof WorldsSubCommand) {
			switch ((WorldsSubCommand) operation) {
				case CREATE: {
					try {
						WorldCreator creator = new WorldCreator(args[1]);
						getPlugin().getServer().createWorld(creator);

						sender.sendMessage(Message.WORLD_CREATED_MESSAGE.with(args[1]));
						return true;
					} catch (ArrayIndexOutOfBoundsException ex) {
					}
					break;
				}
				case DELETE: {
					Server server = getPlugin().getServer();
					try {
						World world = server.getWorld(UUID.fromString(args[1]));
						String name = world.getName();
						server.unloadWorld(world, false);
						server.getWorld(args[1]).getWorldFolder().delete();

						sender.sendMessage(Message.WORLD_DELETED_MESSAGE.with(name));

						return true;
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					}

					break;
				}
				case LIST: {
					sender.sendMessage(Message.WORLD_LIST_MESSAGE.with(getWorldsList(getPlugin().getServer().getWorlds())));

					return true;
				}
				case PORT: {
					try {
						if (sender instanceof Player && !args[1].isEmpty()) {
							World world = getPlugin().getServer().getWorld(args[1]);
							Player player = (Player) sender;
							player.teleport(world.getSpawnLocation(), TeleportCause.COMMAND);

							return true;
						}
					} catch (ArrayIndexOutOfBoundsException ex) {
					}
					break;
				}
				case DELAY: {
					try {
						getPlugin().getConfig().set(WorldsCommand.CONFIG_TELEPORT_DELAY, Integer.parseInt(args[1]));
						getPlugin().saveConfig();
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					}

					sender.sendMessage(Message.WORLD_DELAY_MESSAGE.with(getPlugin().getConfig().get(WorldsCommand.CONFIG_TELEPORT_DELAY)));

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
