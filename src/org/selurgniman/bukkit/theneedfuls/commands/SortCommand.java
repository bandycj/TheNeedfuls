/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class SortCommand implements CommandExecutor {
	public final static String CONFIG_SORT_DISTANCE = "sort.targetDistance";

	private final TheNeedfuls plugin;
	private final ExecutorService executorService;

	/**
	 * @param name
	 * @param plugin
	 */
	public SortCommand(TheNeedfuls plugin) {
		this.plugin = plugin;
		executorService = Executors.newFixedThreadPool(5);
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
		if (sender instanceof Player && sender.hasPermission("theneedfuls.sort")) {
			Player player = (Player) sender;
			ArrayList<Inventory> inventories = new ArrayList<Inventory>();
			Block playerTarget = player.getTargetBlock(null, plugin.getConfig().getInt(CONFIG_SORT_DISTANCE));
			if (playerTarget.getType() == Material.CHEST) {
				Chest chest = (Chest) playerTarget.getState();
				BlockFace[] sides = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
				for (BlockFace blockFace : sides) {
					Block relative = playerTarget.getRelative(blockFace);
					if (relative.getType() == Material.CHEST) {
						Chest relativeChest = (Chest) relative.getState();
						if (blockFace == BlockFace.NORTH || blockFace == BlockFace.EAST) {
							inventories.add(relativeChest.getInventory());
							inventories.add(chest.getInventory());
						} else {
							inventories.add(chest.getInventory());
							inventories.add(relativeChest.getInventory());
						}
					}
				}
			} else {
				inventories.add(player.getInventory());
			}

			if (inventories.size() < 1) {
				return false;
			}
			for (Inventory inventory : inventories) {
				if (inventory == null) {
					return false;
				}
			}

			executorService.submit(new Sorter(inventories, sender));
			return true;
		}
		return false;
	}

	private class Sorter implements Runnable {
		private final ArrayList<Inventory> inventories;
		private final CommandSender sender;

		public Sorter(ArrayList<Inventory> inventories, CommandSender sender) {
			this.inventories = inventories;
			this.sender = sender;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			ItemStack[] hotbar = new ItemStack[9];
			ItemStack[] itemStacks = new ItemStack[0];
			String sortTargetType = ChatColor.GOLD + "container";
			for (Inventory inventory : inventories) {
				itemStacks = concat(itemStacks, inventory.getContents());

				if (inventory instanceof PlayerInventory) {
					hotbar = Arrays.copyOfRange(itemStacks, 0, 9);
					itemStacks = Arrays.copyOfRange(itemStacks, 9, itemStacks.length);
					sortTargetType = ChatColor.GREEN + "inventory";
					break;
				}
			}

			sender.sendMessage(Message.PREFIX + "Sorting " + sortTargetType + ChatColor.WHITE + "...");
			try {
				ArrayList<ItemStack> temp = new ArrayList<ItemStack>();
				for (ItemStack itemStack : itemStacks) {
					if (itemStack != null && itemStack.getAmount() < 64) {
						for (ItemStack notFullItemStack : temp) {
							if (notFullItemStack.getTypeId() == itemStack.getTypeId()
									&& notFullItemStack.getDurability() == itemStack.getDurability()
									&& notFullItemStack.getEnchantments().equals(itemStack.getEnchantments())) {

								int remainingSpace = 64 - itemStack.getAmount();
								int spaceNeeded = notFullItemStack.getAmount();
								if (remainingSpace >= spaceNeeded) {
									itemStack.setAmount(itemStack.getAmount() + spaceNeeded);
									notFullItemStack.setAmount(0);
								} else {
									itemStack.setAmount(64);
									notFullItemStack.setAmount(notFullItemStack.getAmount() - remainingSpace);
								}
							}
						}
						if (itemStack.getAmount() > 0) {
							temp.add(itemStack);
						}
					}
				}

				int collapsedStacks = 0;
				for (int i = 0; i < itemStacks.length; i++) {
					if (itemStacks[i] != null && itemStacks[i].getAmount() < 1) {
						itemStacks[i] = null;
						collapsedStacks++;
					}
				}

				Arrays.sort(itemStacks, new Comparator<ItemStack>() {
					public int compare(ItemStack item1, ItemStack item2) {
						int item1Id = Integer.MAX_VALUE;
						int item1Amount = Integer.MAX_VALUE;
						if (item1 != null) {
							item1Id = item1.getTypeId();
							item1Amount = item1.getAmount();
						}
						int item2Id = Integer.MAX_VALUE;
						int item2Amount = Integer.MAX_VALUE;
						if (item2 != null) {
							item2Id = item2.getTypeId();
							item2Amount = item2.getAmount();
						}

						int result = Integer.compare(item1Id, item2Id);
						if (result == 0) {
							result = Integer.compare(item1Amount, item2Amount) * -1;
						}
						return result;
					}
				});

				for (Inventory inventory : inventories) {
					System.out.println("size:"+itemStacks.length+hotbar.length);
					if (inventory instanceof PlayerInventory && itemStacks.length+hotbar.length == 36) {
						itemStacks = concat(hotbar, itemStacks);
						inventory.setContents(itemStacks);
						break;
					} else {
						ItemStack[] maxItems = Arrays.copyOfRange(itemStacks, 0, inventory.getSize());
						itemStacks = Arrays.copyOfRange(itemStacks, inventory.getSize(), itemStacks.length);
						inventory.setContents(maxItems);
					}
				}
				sender.sendMessage(Message.PREFIX
						+ "Sorting "
						+ sortTargetType
						+ ChatColor.WHITE
						+ " complete. Collapsed "
						+ ChatColor.GREEN
						+ collapsedStacks
						+ ChatColor.WHITE
						+ " stacks!");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private ItemStack[] concat(ItemStack[] A, ItemStack[] B) {
			ItemStack[] C = new ItemStack[A.length + B.length];
			System.arraycopy(A, 0, C, 0, A.length);
			System.arraycopy(B, 0, C, A.length, B.length);

			return C;
		}

	}
}
