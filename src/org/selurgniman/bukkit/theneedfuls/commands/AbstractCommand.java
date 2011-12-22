/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.selurgniman.bukkit.theneedfuls.Message;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;

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
		if (subCommands == null)
			return false;

		ISubCommand operation = null;
		try {
			for (ISubCommand subCommand : subCommands) {
				if (subCommand.toString().equals(args[0].toUpperCase())) {
					operation = subCommand;
				}
			}
		} catch (IllegalArgumentException ex) {
			return false;
		} catch (ArrayIndexOutOfBoundsException ex) {
			return false;
		}

		String option = "";
		if (args.length > 1) {
			option = args[1].toLowerCase();
		}

		if (option.equalsIgnoreCase("help")) {
			sender.sendMessage(operation.getHelp());
			sender.sendMessage(operation.getUsage());

			return true;
		}

		if (!option.isEmpty() && plugin.getServer().getPlayer(option) == null) {
			if (!sender.hasPermission(command.getPermission() + "." + operation.toString().toLowerCase() + "." + option)) {
				sender.sendMessage(String.format(Message.LACK_PERMISSION_MESSAGE.toString(), command.getPermission(), operation.toString().toLowerCase()+"."+option));
				return false;
			}
		} else if (!sender.hasPermission(command.getPermission() + "." + operation.toString().toLowerCase())) {
			sender.sendMessage(String.format(Message.LACK_PERMISSION_MESSAGE.toString(), command.getPermission(), operation.toString().toLowerCase()));
			return false;
		}

		return processCommand(sender, command, label, args, operation, option);
	}

	abstract public boolean processCommand(CommandSender sender, Command command, String label, String[] args, ISubCommand operation, String option);

	public void setSubCommands(ISubCommand[] subCommands) {
		this.subCommands = subCommands;
	}
}
