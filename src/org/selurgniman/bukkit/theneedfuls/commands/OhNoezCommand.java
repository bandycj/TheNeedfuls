/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.selurgniman.bukkit.theneedfuls.Message;
import org.selurgniman.bukkit.theneedfuls.PotionHelper;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.model.Credit;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class OhNoezCommand extends AbstractCommand {
	private final TheNeedfuls plugin;

	/**
	 * @param name
	 * @param plugin
	 */
	public OhNoezCommand(TheNeedfuls plugin) {
		super(plugin);
		this.plugin = plugin;
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
					if (sender instanceof Player) {
						List<ItemStack> droppedItems = plugin.getModel().getLastInventory((Player) sender);
						if (droppedItems.size() > 0) {
							for (ItemStack itemStack : droppedItems) {
								String itemTypeName = itemStack.getType().toString();
								if (itemStack.getType() == Material.INK_SACK) {
									switch (itemStack.getDurability()) {
										case 0xF: {
											itemTypeName = "BONEMEAL";
											break;
										}
										case 0x4: {
											itemTypeName = "LAPIS_LAZULI";
											break;
										}
										case 0x3: {
											itemTypeName = "COCOA_BEANS";
											break;
										}
										case 0x1: {
											itemTypeName = "ROSES";
											break;
										}
									}
								} else if (itemStack.getType() == Material.POTION) {
									for (PotionHelper helper : PotionHelper.values()) {
										if (itemStack.getDurability() == helper.getData()) {
											itemTypeName = helper.getDescription();
										}
									}
								}
								sender.sendMessage(String.format(Message.LIST_ITEM_MESSAGE + "\n", itemTypeName, itemStack.getAmount()));
							}
							sender.sendMessage(String.format(
									Message.LIST_ITEM_MESSAGE + "\n",
									"Experience levels",
									plugin.getModel().getLastExperience((Player) sender)));
						} else {
							sender.sendMessage(Message.NO_ITEMS_MESSAGE + "\n");
						}
						return true;
					}

					return false;
				}
				case CREDITS: {
					if (option.isEmpty() && sender instanceof Player) {
						Player player = (Player) sender;
						sender.sendMessage(String.format(
								Message.AVAILABLE_CREDITS_MESSAGE + "\n",
								player.getName(),
								plugin.getModel().getAvailableCredits(player)));
						return true;
					} else if (args.length > 3) {
						Player player = plugin.getServer().getPlayer(args[2]);
						int amount = Integer.parseInt(args[3]);
						if (option.equalsIgnoreCase("ADD")) {
							plugin.getModel().addAvailableCredits(player, amount);
						} else if (option.equalsIgnoreCase("SET")) {
							plugin.getModel().setAvailableCredits(player, Integer.parseInt(args[2]));
						}
						sender.sendMessage(String.format(
								Message.AVAILABLE_CREDITS_MESSAGE + "\n",
								player.getName(),
								plugin.getModel().getAvailableCredits(player)));
						return true;
					}

					return false;
				}
				case CLAIM: {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						List<ItemStack> droppedItems = plugin.getModel().getLastInventory(player);
						Integer droppedExp = plugin.getModel().getLastExperience(player);

						if (droppedItems != null) {
							if (plugin.getModel().getAvailableCredits(player) > 0) {
								for (ItemStack itemStack : droppedItems) {
									player.getInventory().addItem(itemStack);
									sender.sendMessage(String.format(
											Message.CREDITED_BACK_MESSAGE + "\n",
											itemStack.getType().toString(),
											itemStack.getAmount()));
								}
								player.setLevel(player.getLevel() + droppedExp);

								plugin.getModel().useAvailableCredit(player);
								plugin.getModel().setLastInventory(player, new ArrayList<ItemStack>(), 0);
							} else {
								sender.sendMessage(String.format(Message.AVAILABLE_CREDITS_MESSAGE + "\n", player.getName(), plugin
										.getModel()
										.getAvailableCredits(player)));
							}
						} else {
							sender.sendMessage(Message.NO_ITEMS_MESSAGE + "\n");
						}
						return true;
					}

					return false;
				}
				case WORLDS: {
					if (args.length > 2) {
						List<String> worldIds = plugin.getConfig().getStringList(Credit.OHNOEZ_WORLDS_KEY);
						World world = plugin.getServer().getWorld(args[2]);
						int worldIndex = Collections.binarySearch(worldIds, world.getUID().toString());

						if (option.equalsIgnoreCase("add") && worldIndex < 0) {
							worldIds.add(world.getUID().toString());
							Collections.sort(worldIds);
						} else if (option.equalsIgnoreCase("remove") && worldIndex > -1) {
							worldIds.remove(worldIndex);
						}

						plugin.getConfig().set(Credit.OHNOEZ_WORLDS_KEY, worldIds);
						plugin.saveConfig();
					}

					List<String> worlds = new ArrayList<String>();
					for (String worldId : plugin.getConfig().getStringList(Credit.OHNOEZ_WORLDS_KEY)) {
						World world = plugin.getServer().getWorld(UUID.fromString(worldId));
						if (world != null) {
							switch (world.getEnvironment()) {
								case NORMAL: {
									worlds.add(ChatColor.GREEN + world.getName() + ChatColor.WHITE);
									break;
								}
								case NETHER: {
									worlds.add(ChatColor.RED + world.getName() + ChatColor.WHITE);
									break;
								}
								case THE_END: {
									worlds.add(ChatColor.DARK_BLUE + world.getName() + ChatColor.WHITE);
									break;
								}
							}
						}
					}

					sender.sendMessage(String.format(Message.OHNOEZ_WORLDS_MESSAGE.toString(), worlds).replace("[", "").replace("]", ""));
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
