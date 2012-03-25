/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.misc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.commands.AbstractCommand;
import org.selurgniman.bukkit.theneedfuls.commands.ISubCommand;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class DebugCommand extends AbstractCommand {
	/**
	 * @param getPlugin
	 *            ()
	 */
	public DebugCommand(TheNeedfuls plugin) {
		super(plugin);
		this.setSubCommands(DebugSubCommand.values());
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
		if (operation != null && operation instanceof DebugSubCommand) {
			FileConfiguration config = this.getPlugin().getConfig();
			Boolean debug = config.getBoolean("Debug");
			Boolean debugPlayer = config.getBoolean("DebugPlayer");
			switch ((DebugSubCommand) operation) {
				case ON: {
					if (!debug) {
						debug = true;
					}
					
					break;
				}
				case OFF: {
					if (debug) {
						debug = false;
					}
					
					break;
				}
				case PLAYER: {
					if (debugPlayer) {
						debugPlayer = false;
					} else {
						debugPlayer = true;
					}
					break;
				}
			}
			
			config.set("Debug", debug);
			config.set("DebugPlayer", debugPlayer);
			this.getPlugin().saveConfig();
			this.getPlugin().reloadConfig();
			sender.sendMessage(Message.DEBUG_MESSAGE.with(debug, debugPlayer));

			return true;
		}
		return false;
	}

	public static enum DebugSubCommand
			implements
			ISubCommand {
		ON(
				ChatColor.GREEN + "ON: " + ChatColor.WHITE + "turns debug logging on.",
				"/tnd on"),
		OFF(
				ChatColor.GREEN + "OFF: " + ChatColor.WHITE + "turns debug logging off.",
				"/tnd off"),
		PLAYER(
				ChatColor.GREEN + "PLAYER: " + ChatColor.WHITE + "toggles logging to ops.",
				"/tnd player");

		private final String help;
		private final String usage;

		private DebugSubCommand(String help, String usage) {
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
