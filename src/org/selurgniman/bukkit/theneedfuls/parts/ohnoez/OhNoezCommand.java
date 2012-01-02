/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.ohnoez;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.commands.AbstractCommand;
import org.selurgniman.bukkit.theneedfuls.commands.ISubCommand;
import org.selurgniman.bukkit.theneedfuls.helpers.InkSackHelper;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.helpers.PotionHelper;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;
import org.selurgniman.bukkit.theneedfuls.model.dao.Credit;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class OhNoezCommand extends AbstractCommand {
	private final OhNoezModel model;
	/**
	 * @param name
	 * @param plugin
	 */
	public OhNoezCommand(TheNeedfuls plugin) {
		super(plugin);
		this.model = (OhNoezModel)Model.getCommandModel(CommandType.OHNOEZ);
		this.setSubCommands(OhNoezSubCommand.values());
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
		if (operation instanceof OhNoezSubCommand) {
			switch ((OhNoezSubCommand) operation) {
				case LIST: {
					Player player = sender.getServer().getPlayer(option);
					if (sender instanceof Player || player != null) {
						if (player == null) {
							player = (Player) sender;
						}
						List<ItemStack> droppedItems = model.getLastInventory(player);
						if (droppedItems.size() > 0) {
							for (ItemStack itemStack : droppedItems) {
								String itemTypeName = itemStack.getType().toString();
								if (itemStack.getType() == Material.INK_SACK) {
									itemTypeName = InkSackHelper.getType(itemStack.getDurability()).getName();
								} else if (itemStack.getType() == Material.POTION) {
									for (PotionHelper helper : PotionHelper.values()) {
										if (itemStack.getDurability() == helper.getData()) {
											itemTypeName = helper.getDescription();
										}
									}
								}
								sender.sendMessage(String.format(Message.LIST_ITEM_MESSAGE + "\n", itemTypeName, itemStack.getAmount()));
							}
							SimpleEntry<Integer, Float> lastExp = model.getLastExperience(player);
							sender.sendMessage(String.format(Message.LIST_ITEM_MESSAGE.toString(), "Experience levels", lastExp.getKey()));
						} else {
							sender.sendMessage(Message.NO_ITEMS_MESSAGE.toString());
						}
						return true;
					}

					return false;
				}
				case CREDITS: {
					if (option.isEmpty() && sender instanceof Player) {
						Player player = (Player) sender;
						sender.sendMessage(String.format(
								Message.AVAILABLE_CREDITS_MESSAGE.toString(),
								player.getName(),
								model.getAvailableCredits(player)));
						return true;
					} else if (args.length > 3) {
						Player player = getPlugin().getServer().getPlayer(args[2]);
						int amount = Integer.parseInt(args[3]);
						if (option.equalsIgnoreCase("ADD")) {
							model.addAvailableCredits(player, amount);
						} else if (option.equalsIgnoreCase("SET")) {
							model.setAvailableCredits(player, amount);
						}
						sender.sendMessage(String.format(
								Message.AVAILABLE_CREDITS_MESSAGE.toString(),
								player.getName(),
								model.getAvailableCredits(player)));
						return true;
					}

					return false;
				}
				case CLAIM: {
					if (sender instanceof Player && this.getPlugin().getModel().isCommandWorld(CommandType.OHNOEZ,((Player) sender).getWorld())) {
						Player player = (Player) sender;
						List<ItemStack> droppedItems = model.getLastInventory(player);
						Entry<Integer, Float> droppedExp = model.getLastExperience(player);

						if (droppedItems != null) {
							if (model.getAvailableCredits(player) > 0) {
								for (ItemStack itemStack : droppedItems) {
									player.getInventory().addItem(itemStack);
									sender.sendMessage(String.format(
											Message.CREDITED_BACK_MESSAGE.toString(),
											itemStack.getType().toString(),
											itemStack.getAmount()));
								}
								player.setLevel(player.getLevel() + droppedExp.getKey());
								player.setExp(player.getExp() + droppedExp.getValue());

								model.useAvailableCredit(player);
							} else {
								sender.sendMessage(String.format(Message.AVAILABLE_CREDITS_MESSAGE.toString(), player.getName(), model.getAvailableCredits(player)));
							}
						} else {
							sender.sendMessage(Message.NO_ITEMS_MESSAGE.toString());
						}
						return true;
					}

					return false;
				}
				case WORLDS: {
					if (args.length > 2) {
						addRemoveWorld(option, args[2], Credit.OHNOEZ_WORLDS_KEY);
					}
					
					sender.sendMessage(String.format(Message.OHNOEZ_WORLDS_MESSAGE.toString(), getWorldsList(Credit.OHNOEZ_WORLDS_KEY)));
					return true;
				}

				default: {
					return false;
				}
			}
		}
		return false;
	}

	public static enum OhNoezSubCommand
			implements
			ISubCommand {
		LIST(
				ChatColor.GREEN + "OhNoez List: " + ChatColor.WHITE + "shows the list of the items dropped during the last death.",
				"/ohnoez list"),
		CREDITS(
				ChatColor.GREEN + "OhNoez Credits: " + ChatColor.WHITE + "displays, adds and sets the credits of a player.",
				"/ohnoez credits [<add|set> <player> <amount>]"),
		CLAIM(
				ChatColor.GREEN + "OhNoez Claim: " + ChatColor.WHITE + "uses a credit if one is available.",
				"/ohnoez claim"),
		WORLDS(
				ChatColor.GREEN + "OhNoez Worlds: " + ChatColor.WHITE + "sets the worlds to track death drops for.",
				"/ohnoez worlds <add|remove> <world name>");

		private final String help;
		private final String usage;

		private OhNoezSubCommand(String help, String usage) {
			this.help = help;
			this.usage = usage;
		}

		@Override
		public String getHelp() {
			return this.help;
		}

		@Override
		public String getUsage() {
			return this.usage;
		}
	}
}
