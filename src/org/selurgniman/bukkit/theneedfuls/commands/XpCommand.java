/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class XpCommand extends AbstractCommand {
	/**
	 * @param getPlugin()
	 */
	public XpCommand(TheNeedfuls plugin) {
		super(plugin);
		this.setSubCommands(XpSubCommand.values());
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
		if (operation instanceof XpSubCommand) {
			Integer amount = 0;
			if (args.length > 2) {
				try {
					amount = Integer.parseInt(args[2]);
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
					return false;
				}
			}

			Player player = getPlugin().getServer().getPlayer(option);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Player not found: " + ChatColor.WHITE + option);
				return true;
			}

			switch ((XpSubCommand) operation) {
				case SHOW: {
					sender.sendMessage(String.format(Message.SHOW_EXPERIENCE_MESSAGE.toString(), player.getDisplayName(), player.getLevel()));
					break;
				}
				case GIVE: {
					if (sender instanceof Player && !sender.equals(player) && amount > 0) {
						if (((Player) sender).getLevel() >= amount) {
							player.setLevel(player.getLevel() + amount);
							((Player) sender).setLevel(((Player) sender).getLevel() - amount);
						} else {
							sender.sendMessage(String.format(Message.INSUFFICIENT_EXPERIENCE_MESSAGE.toString(),amount));
						}
					}
					break;
				}
				case TAKE: {
					if (amount > 0) {
						player.setLevel(player.getLevel() - amount);
					}
					break;
				}
				case SET: {
					if (amount > 0) {
						player.setLevel(amount);
					}
					break;
				}
			}
			sender.sendMessage(String.format(Message.SHOW_EXPERIENCE_MESSAGE.toString(), player.getDisplayName(), player.getLevel()));

			return true;
		}
		return false;
	}

	public static enum XpSubCommand
			implements
			ISubCommand {
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
