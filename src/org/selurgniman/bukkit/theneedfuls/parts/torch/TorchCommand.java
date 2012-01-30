/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.torch;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.commands.AbstractCommand;
import org.selurgniman.bukkit.theneedfuls.commands.ISubCommand;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;
import org.selurgniman.bukkit.theneedfuls.model.dao.Torch;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class TorchCommand extends AbstractCommand {
	private final TorchModel model;

	/**
	 * @param name
	 * @param getPlugin
	 *            ()
	 */
	public TorchCommand(TheNeedfuls plugin) {
		super(plugin);
		this.model = (TorchModel) Model.getCommandModel(CommandType.TORCH);
		this.setSubCommands(TorchSubCommand.values());
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
		if (operation != null && operation instanceof TorchSubCommand) {
			switch ((TorchSubCommand) operation) {
				case AGE: {
					try {
						getPlugin().getConfig().set(Torch.TORCH_AGE_KEY, Integer.parseInt(args[1]));
						getPlugin().saveConfig();
						getPlugin().getModel().loadWorldControls();
						getPlugin().initTorchTask();
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					}
					sender.sendMessage(Message.TORCH_AGE_MESSAGE.with(getPlugin().getConfig().get(Torch.TORCH_AGE_KEY)));
					
					return true;
				}
				case REFRESH: {
					try {
						getPlugin().getConfig().set(Torch.TORCH_REFRESH_KEY, Integer.parseInt(args[1]));
						getPlugin().saveConfig();
						getPlugin().getModel().loadWorldControls();
						getPlugin().initTorchTask();
					} catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
					}
					sender.sendMessage(Message.TORCH_REFRESH_MESSAGE.with(getPlugin().getConfig().get(Torch.TORCH_REFRESH_KEY)));
					
					return true;
				}
				case COUNT: {
					int torchCount = model.getTorchCount();
					sender.sendMessage(Message.TORCH_COUNT_MESSAGE.with(torchCount));

					return true;
				}
				case EXPIRE: {
					int torchCount = model.getTorchCount();

					model.expireAllTorches();
					sender.sendMessage(Message.TORCH_EXPIRE_MESSAGE.with(torchCount));
					torchCount = model.getTorchCount();
					sender.sendMessage(Message.TORCH_COUNT_MESSAGE.with(torchCount));

					return true;
				}
				case WORLDS: {
					try {
						addRemoveWorld(args[1], args[2], Torch.TORCH_WORLDS_KEY);
						getPlugin().getModel().loadWorldControls();
						getPlugin().initTorchTask();
					} catch (ArrayIndexOutOfBoundsException ex) {
					}
					sender.sendMessage(Message.TORCH_WORLDS_MESSAGE.with(getWorldsList(Torch.TORCH_WORLDS_KEY)));
					
					return true;
				}
			}
		}
		return false;
	}

	public static enum TorchSubCommand
			implements
			ISubCommand {
		AGE(
				ChatColor.GREEN + "Torch Age: " + ChatColor.WHITE + "displays and sets the maximum age of torches in seconds.",
				"/tnt age <number of seconds>"),
		REFRESH(
				ChatColor.GREEN + "Torch Refresh: " + ChatColor.WHITE + "displays and sets the frequency to check for expired torches.",
				"/tnt refresh"),
		COUNT(
				ChatColor.GREEN + "Torch Count: " + ChatColor.WHITE + "displays the total count of torches being tracked.",
				"/tnt count"),
		EXPIRE(
				ChatColor.GREEN + "Torch Expire: " + ChatColor.WHITE + "exires all torches being tracked.",
				"/tnt expire"),
		WORLDS(
				ChatColor.GREEN + "Torch Worlds: " + ChatColor.WHITE + "sets the worlds to track torches for.",
				"/tnt worlds <add|remove> <world name>");

		private final String help;
		private final String usage;

		private TorchSubCommand(String help, String usage) {
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
