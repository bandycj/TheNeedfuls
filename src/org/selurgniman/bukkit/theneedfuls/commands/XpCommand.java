/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.selurgniman.bukkit.theneedfuls.Message;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class XpCommand implements CommandExecutor {
	private final TheNeedfuls plugin;

	/**
	 * @param name
	 * @param plugin
	 */
	public XpCommand(TheNeedfuls plugin) {
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
		XpSubCommand operation = null;
		try {
			operation = XpSubCommand.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException ex) {
			return false;
		} catch (ArrayIndexOutOfBoundsException ex) {
			return false;
		}

		String option = "";
		Integer amount = 0;
		if (args.length > 1) {
			option = args[1];
		}

		if (args.length > 2) {
			try {
				amount = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				return false;
			}
		}

		if (option.equalsIgnoreCase("help")) {
			sender.sendMessage(operation.getHelp());
			sender.sendMessage(operation.getUsage());

			return true;
		}

		if (!sender.hasPermission(command.getPermission() + "." + operation.toString().toLowerCase())) {
			sender.sendMessage(String.format(Message.LACK_PERMISSION_MESSAGE.toString(),command.getPermission(),operation.toString().toLowerCase()));
			return true;
		}

		Player player = plugin.getServer().getPlayer(option);
		if (player == null) {
			sender.sendMessage(ChatColor.RED + "Player not found: " + ChatColor.WHITE + option);
			return true;
		}

		switch (operation) {
			case SHOW: {
				sender.sendMessage(String.format(Message.SHOW_EXPERIENCE_MESSAGE.toString(), player.getDisplayName(), player.getLevel()));
				return true;
			}
			case GIVE: {
				if (amount > 0) {
					player.setLevel(player.getLevel() + amount);
					return true;
				}
				break;
			}
			case TAKE: {
				if (amount > 0) {
					player.setLevel(player.getLevel() - amount);
					return true;
				}
				break;
			}
			case SET: {
				if (amount > 0) {
					player.setLevel(amount);
					return true;
				}
				break;
			}
			default: {
				sender.sendMessage(String.format(Message.SHOW_EXPERIENCE_MESSAGE.toString(), player.getDisplayName(), player.getLevel()));
				break;
			}
		}
		return false;
	}

	public enum XpSubCommand {
		SHOW(
				ChatColor.GREEN + "XP Show: " + ChatColor.WHITE + "shows the experience level of a player.",
				"/tnx show <player>"),
		GIVE(
				ChatColor.GREEN + "XP Give: " + ChatColor.WHITE + "gives the requested number of levels to a player.",
				"/tnx give <player> <level>"),
		TAKE(
				ChatColor.GREEN + "XP Take: " + ChatColor.WHITE + "removes experience levels from a player.",
				"/tnx take <player> <level>"),
		SET(
				ChatColor.GREEN + "XP Set: " + ChatColor.WHITE + "sets a player's experience level.",
				"/tnx set <player> <level>");

		private final String help;
		private final String usage;

		private XpSubCommand(String help, String usage) {
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
