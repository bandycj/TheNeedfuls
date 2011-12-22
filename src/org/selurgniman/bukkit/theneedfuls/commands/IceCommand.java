/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.selurgniman.bukkit.theneedfuls.Message;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class IceCommand extends AbstractCommand {
	private final TheNeedfuls plugin;

	/**
	 * @param name
	 * @param plugin
	 */
	public IceCommand(TheNeedfuls plugin) {
		super(plugin);
		this.plugin = plugin;
		this.setSubCommands(IceSubCommand.values());
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
		if (operation instanceof IceSubCommand) {
			switch ((IceSubCommand) operation) {
				case QUANTITY: {
					try {
						plugin.getConfig().set("iceMaker.quantity", Integer.parseInt(option));
						plugin.saveConfig();
					} catch (NumberFormatException ex) {

					}

					sender.sendMessage(String.format(Message.ICE_QUANTITY_MESSAGE.toString(), plugin.getConfig().get("iceMaker.quantity")));
					return true;
				}
			}
		}

		return false;
	}

	public static enum IceSubCommand
			implements
			ISubCommand {
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
