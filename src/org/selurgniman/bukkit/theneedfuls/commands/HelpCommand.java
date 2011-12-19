/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.selurgniman.bukkit.theneedfuls.commands.IceCommand.IceSubCommand;
import org.selurgniman.bukkit.theneedfuls.commands.TorchCommand.TorchSubCommand;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class HelpCommand implements CommandExecutor {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender
	 * , org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		TheNeedfulsCommand cmd = null;
		try {
			cmd = TheNeedfulsCommand.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException ex) {
			return false;
		} catch (ArrayIndexOutOfBoundsException ex) {
			return helpHelp(sender);
		}

		String operation = "";
		if (args.length > 1) {
			operation = args[1];
		}

		switch (cmd) {
			case TORCH: {
				return torchHelp(sender, operation);
			}
			case ICE: {
				return iceHelp(sender, operation);
			}
			case HELP: {
				return helpHelp(sender);
			}
		}

		return false;
	}

	private boolean torchHelp(CommandSender sender, String operation) {
		try {
			TorchSubCommand subCommand = TorchSubCommand.valueOf(operation.toUpperCase());
			sender.sendMessage(subCommand.getHelp());
			sender.sendMessage(subCommand.getUsage());
			return true;
		} catch (IllegalArgumentException ex) {

		}
		return false;
	}

	private boolean iceHelp(CommandSender sender, String operation) {
		try {
			IceSubCommand subCommand = IceSubCommand.valueOf(operation.toUpperCase());
			sender.sendMessage(subCommand.getHelp());
			sender.sendMessage(subCommand.getUsage());
			return true;
		} catch (IllegalArgumentException ex) {

		}
		return false;
	}

	private boolean helpHelp(CommandSender sender) {
		for (TorchSubCommand subCommand : TorchSubCommand.values()) {
			sender.sendMessage(subCommand.getHelp());
			sender.sendMessage(subCommand.getUsage());
		}
		for (IceSubCommand subCommand : IceSubCommand.values()) {
			sender.sendMessage(subCommand.getHelp());
			sender.sendMessage(subCommand.getUsage());
		}
		return false;
	}

	private enum TheNeedfulsCommand {
		TORCH,
		ICE,
		HELP;
	}
}