/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.ice;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.commands.AbstractCommand;
import org.selurgniman.bukkit.theneedfuls.commands.ISubCommand;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class IceCommand extends AbstractCommand {
	private final TheNeedfuls plugin;
	public static final String CONFIG_ICE_QUANTITY = "iceMaker.quantity";
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
	public boolean processCommand(CommandSender sender, Command command, String label, String[] args, ISubCommand operation) {
		if (operation != null && operation instanceof IceSubCommand) {
			switch ((IceSubCommand) operation) {
				case QUANTITY: {
					try {
						plugin.getConfig().set("iceMaker.quantity", Integer.parseInt(args[1]));
						plugin.saveConfig();
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					}

					sender.sendMessage(Message.ICE_QUANTITY_MESSAGE.with(plugin.getConfig().getInt("iceMaker.quantity")));
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
