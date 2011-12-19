/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.selurgniman.bukkit.theneedfuls.Message;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class IceCommand implements CommandExecutor {
	private final TheNeedfuls plugin;

	/**
	 * @param name
	 * @param plugin
	 */
	public IceCommand(TheNeedfuls plugin) {
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
		IceSubCommand operation = null;
		try {
			operation = IceSubCommand.valueOf(args[0].toUpperCase());
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
			case QUANTITY: {
				try {
					plugin.getConfig().set("iceMaker.quantity", Integer.parseInt(option));
					plugin.saveConfig();					
				} catch (NumberFormatException ex) {

				}
				
				sender.sendMessage(String.format(Message.ICE_QUANTITY_MESSAGE.toString(), plugin.getConfig().get("iceMaker.quantity")));
				return true;
			}

			default: {
				return false;
			}
		}
	}

	public enum IceSubCommand {
		QUANTITY(
				ChatColor.GREEN + "Ice Quantity: " + ChatColor.WHITE + "displays and sets the size of the stack of ice dispensed per water bucket.",
				"/tni quantity <stack size 1-64>");

		private final String help;
		private final String usage;

		private IceSubCommand(String help, String usage) {
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
