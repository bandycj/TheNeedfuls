/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.parts.ice.IceCommand.IceSubCommand;
import org.selurgniman.bukkit.theneedfuls.parts.misc.XpCommand.XpSubCommand;
import org.selurgniman.bukkit.theneedfuls.parts.ohnoez.OhNoezCommand.OhNoezSubCommand;
import org.selurgniman.bukkit.theneedfuls.parts.torch.TorchCommand.TorchSubCommand;
import org.selurgniman.bukkit.theneedfuls.parts.worlds.WorldsCommand.WorldsSubCommand;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class HelpCommand extends AbstractCommand {

	public HelpCommand(TheNeedfuls plugin) {
		super(plugin);
		this.setSubCommands(HelpSubCommand.values());
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
		try {
			if (operation instanceof HelpSubCommand) {
				String option = args[0].toUpperCase();
				switch ((HelpSubCommand) operation) {
					case TORCH: {
						sendHelp(sender, TorchSubCommand.valueOf(option));
					}
					case ICE: {
						sendHelp(sender, IceSubCommand.valueOf(option));
					}
					case XP: {
						sendHelp(sender, XpSubCommand.valueOf(option));
					}
					case WORLDS: {
						sendHelp(sender, WorldsSubCommand.valueOf(option));
					}
					case OHNOEZ: {
						sendHelp(sender, OhNoezSubCommand.valueOf(option));
					}
					case SORT: {
						sender.sendMessage("This command has no parameters.");
					}
					case HELP: {
						sendHelp(sender, HelpSubCommand.valueOf(option));
					}
				}
			}
			return true;
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
		}

		return false;
	}

	private void sendHelp(CommandSender sender, ISubCommand subCommand) {
		sender.sendMessage(subCommand.getHelp());
		sender.sendMessage(subCommand.getUsage());
	}

	private enum HelpSubCommand
			implements
			ISubCommand {
		TORCH(
				ChatColor.GREEN + "Help Torch: " + ChatColor.WHITE + "displays detailed help for the tnt command.",
				"/tnh torch"),
		ICE(
				ChatColor.GREEN + "Help Ice: " + ChatColor.WHITE + "displays detailed help for the tni command.",
				"/tnh ice"),
		SHEEP(
				ChatColor.GREEN + "Help Cheep: " + ChatColor.WHITE + "displays detailed help for the tns command.",
				"/tnh sheep"),
		XP(
				ChatColor.GREEN + "Help XP: " + ChatColor.WHITE + "displays detailed help for the tnx command.",
				"/tnh xp"),
		WORLDS(
				ChatColor.GREEN + "Help Worlds: " + ChatColor.WHITE + "displays detailed help for the tnw command.",
				"/tnh worlds"),
		OHNOEZ(
				ChatColor.GREEN + "Help OhNoez: " + ChatColor.WHITE + "displays detailed help for the ohnoez command.",
				"/tnh ohnoez"),
		SORT(
				ChatColor.GREEN + "Help Sort: " + ChatColor.WHITE + "displays detailed help for the sort command.",
				"/tnh sort"),
		HELP(
				ChatColor.GREEN + "Help Help: " + ChatColor.WHITE + "displays detailed help for the help command.",
				"/tnh help");

		private final String help;
		private final String usage;

		private HelpSubCommand(String help, String usage) {
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