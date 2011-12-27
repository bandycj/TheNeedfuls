/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class SheepCommand extends AbstractCommand {
	private final TheNeedfuls plugin;
	public static final String CONFIG_SHEEP_REFRESH = "sheep.refresh";
	/**
	 * @param name
	 * @param plugin
	 */
	public SheepCommand(TheNeedfuls plugin) {
		super(plugin);
		this.plugin = plugin;
		this.setSubCommands(SheepSubCommand.values());
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
		if (operation instanceof SheepSubCommand) {
			switch ((SheepSubCommand) operation) {
				case REFRESH: {
					try {
						plugin.getConfig().set("sheep.refresh", Integer.parseInt(option));
						plugin.saveConfig();
						plugin.initSheepTask();
					} catch (NumberFormatException ex) {

					}

					sender.sendMessage(String.format(Message.SHEEP_REFRESH_MESSAGE.toString(), plugin.getConfig().get("sheep.refresh")));
					return true;
				}
			}
		}

		return false;
	}

	public static enum SheepSubCommand
			implements
			ISubCommand {
		REFRESH(
				ChatColor.GREEN + "Sheep Refresh: " + ChatColor.WHITE + "displays and sets the refresh rate for sheep unshearing.",
				"/tns refresh <refresh in seconds>");

		private final String help;
		private final String usage;

		private SheepSubCommand(String help, String usage) {
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
