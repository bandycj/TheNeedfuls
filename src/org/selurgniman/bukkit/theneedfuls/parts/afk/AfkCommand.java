///**
// * 
// */
//package org.selurgniman.bukkit.theneedfuls.parts.afk;
//
//import org.bukkit.ChatColor;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
//import org.selurgniman.bukkit.theneedfuls.commands.AbstractCommand;
//import org.selurgniman.bukkit.theneedfuls.commands.ISubCommand;
//import org.selurgniman.bukkit.theneedfuls.helpers.Message;
//
///**
// * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
// *         on: Dec 18, 2011
// */
//public class AfkCommand extends AbstractCommand {
//	private final TheNeedfuls plugin;
//	public static final String CONFIG_AFK_DELAY = "afk.delay";
//	
//	/**
//	 * @param name
//	 * @param plugin
//	 */
//	public AfkCommand(TheNeedfuls plugin) {
//		super(plugin);
//		this.plugin = plugin;
//		this.setSubCommands(AfkSubCommand.values());
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender
//	 * , org.bukkit.command.Command, java.lang.String, java.lang.String[])
//	 */
//	@Override
//	public boolean processCommand(CommandSender sender, Command command, String label, String[] args, ISubCommand operation, String option) {
//		if (operation != null && operation instanceof AfkSubCommand) {
//			switch ((AfkSubCommand) operation) {
//				case DELAY: {
//					try {
//						int newDelay = Integer.parseInt(option);
//						plugin.getConfig().set(CONFIG_AFK_DELAY, newDelay);
//						plugin.saveConfig();
//						AfkPlayerListener.setDelay(newDelay);
//					} catch (NumberFormatException ex) {
//						
//					}
//
//					sender.sendMessage(Message.AFK_DELAY_MESSAGE.with(plugin.getConfig().get(CONFIG_AFK_DELAY)));
//					return true;
//				}
//			}
//		} else if (operation == null && sender instanceof Player){
//			Boolean afk = !AfkPlayerListener.isPlayerActive((Player)sender);
//			AfkPlayerListener.setPlayerActive((Player)sender, afk);
//			ChatColor chatColor = ChatColor.GREEN;
//			if (!afk){
//				chatColor = ChatColor.RED;
//			}
//			
//			sender.sendMessage(Message.AFK_SET_MESSAGE.with(chatColor+afk.toString()));
//			return true;
//		}
//
//		return false;
//	}
//
//	public static enum AfkSubCommand
//			implements
//			ISubCommand {
//		DELAY(
//				ChatColor.GREEN + "Afk Delay: " + ChatColor.WHITE + "sets the amount of idle time in seconds until a player is marked afk.",
//				"/afk delay <amount of seconds>");
//
//		private final String help;
//		private final String usage;
//
//		private AfkSubCommand(String help, String usage) {
//			this.help = help;
//			this.usage = usage;
//		}
//
//		public String getHelp() {
//			return this.help;
//		}
//
//		public String getUsage() {
//			return this.usage;
//		}
//	}
//}
