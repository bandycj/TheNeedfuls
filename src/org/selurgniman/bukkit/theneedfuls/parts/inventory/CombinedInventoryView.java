/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.server.Container;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * 
 */
public class CombinedInventoryView extends InventoryView {
	private HumanEntity player;
	private CombinedInventory combinedInventory;

	public CombinedInventoryView(HumanEntity player, List<Inventory> viewing) {
		// TODO: Should we make sure it really IS a CraftHumanEntity first? And
		// a CraftInventory?
		this.player = player;
		this.combinedInventory = new CombinedInventory(viewing);
	}

	@Override
	public Inventory getTopInventory() {
		return combinedInventory;
	}

	@Override
	public Inventory getBottomInventory() {
		return player.getInventory();
	}

	@Override
	public HumanEntity getPlayer() {
		return player;
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}

	@Override
	public void setItem(int slot, ItemStack item) {
		combinedInventory.setItem(slot, item);
	}

	@Override
	public ItemStack getItem(int slot) {
		return combinedInventory.getItem(slot);
	}

	public static SlotType getSlotType(InventoryView inventory, int slot) {
		SlotType type = SlotType.CONTAINER;
		if (slot < inventory.getTopInventory().getSize()) {
			switch (inventory.getType()) {
				case FURNACE:
					if (slot == 2) {
						type = SlotType.RESULT;
					} else if (slot == 1) {
						type = SlotType.FUEL;
					}
					break;
				case BREWING:
					if (slot == 0) {
						type = SlotType.FUEL;
					} else {
						type = SlotType.CRAFTING;
					}
					break;
				case ENCHANTING:
					type = SlotType.CRAFTING;
					break;
				case WORKBENCH:
				case CRAFTING:
					if (slot == 0) {
						type = SlotType.RESULT;
					} else {
						type = SlotType.CRAFTING;
					}
					break;
				default:
					// Nothing to do, it's a CONTAINER slot
			}
		} else {
			if (slot == -999) {
				type = SlotType.OUTSIDE;
			} else if (inventory.getType() == InventoryType.CRAFTING && slot < 9) {
				type = SlotType.ARMOR;
			} else if (slot >= (inventory.countSlots() - 9)) {
				type = SlotType.QUICKBAR;
			}
		}
		return type;
	}

	private class CombinedInventory implements Inventory {
		private final List<Inventory> inventories;
		private final int size;

		public CombinedInventory(List<Inventory> inventories) {
			this.inventories = inventories;
			int counter = 0;
			for (Inventory inventory : inventories) {
				counter += inventory.getSize();
			}
			this.size = counter;
		}

		@Override
		public int getSize() {
			return this.size;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#getMaxStackSize()
		 */
		@Override
		public int getMaxStackSize() {
			if (inventories.size() > 0) {
				return inventories.get(0).getMaxStackSize();
			}
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#setMaxStackSize(int)
		 */
		@Override
		public void setMaxStackSize(int size) {
			// not implemented
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#getName()
		 */
		@Override
		public String getName() {
			return "WiperWorks: Combined Inventory";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#getItem(int)
		 */
		@Override
		public ItemStack getItem(int index) {
			for (Inventory inventory : inventories) {
				if (index > inventory.getSize()) {
					index -= inventory.getSize();
				} else {
					return inventory.getItem(index);
				}
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#setItem(int,
		 * org.bukkit.inventory.ItemStack)
		 */
		@Override
		public void setItem(int index, ItemStack item) {
			for (Inventory inventory : inventories) {
				if (index > inventory.getSize()) {
					index -= inventory.getSize();
				} else {
					inventory.setItem(index, item);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.bukkit.inventory.Inventory#addItem(org.bukkit.inventory.ItemStack
		 * [])
		 */
		@Override
		public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
			HashMap<Integer, ItemStack> leftOvers = new HashMap<Integer, ItemStack>();
			for (Integer i = -0; i < items.length; i++) {
				leftOvers.put(i, items[i]);
			}

			for (Inventory inventory : inventories) {
				ItemStack[] itemStacks = new ItemStack[leftOvers.size()];
				for (int i = 0; i < leftOvers.size(); i++) {
					itemStacks[i] = leftOvers.get(i);
				}
				leftOvers = inventory.addItem(itemStacks);
			}
			return leftOvers;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.bukkit.inventory.Inventory#removeItem(org.bukkit.inventory.ItemStack
		 * [])
		 */
		@Override
		public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
			HashMap<Integer, ItemStack> leftOvers = new HashMap<Integer, ItemStack>();
			for (Integer i = -0; i < items.length; i++) {
				leftOvers.put(i, items[i]);
			}

			for (Inventory inventory : inventories) {
				ItemStack[] itemStacks = new ItemStack[leftOvers.size()];
				for (int i = 0; i < leftOvers.size(); i++) {
					itemStacks[i] = leftOvers.get(i);
				}
				leftOvers = inventory.removeItem(itemStacks);
			}
			return leftOvers;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#getContents()
		 */
		@Override
		public ItemStack[] getContents() {
			ArrayList<ItemStack> combined = new ArrayList<ItemStack>();

			for (Inventory inventory : inventories) {
				combined.addAll(Arrays.asList(inventory.getContents()));
			}

			return combined.toArray(new ItemStack[combined.size()]);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.bukkit.inventory.Inventory#setContents(org.bukkit.inventory.ItemStack
		 * [])
		 */
		@Override
		public void setContents(ItemStack[] items) {
			int srcPos = 0;
			for (Inventory inventory : inventories) {
				ItemStack[] invItems = new ItemStack[inventory.getSize()];
				System.arraycopy(items, srcPos, invItems, 0, inventory.getSize() - 1);
				srcPos += inventory.getSize() - 1;
				inventory.setContents(invItems);
				if (srcPos >= items.length) {
					break;
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#contains(int)
		 */
		@Override
		public boolean contains(int materialId) {
			for (Inventory inventory : inventories) {
				if (inventory.contains(materialId)) {
					return true;
				}
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#contains(org.bukkit.Material)
		 */
		@Override
		public boolean contains(Material material) {
			return contains(material.getId());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.bukkit.inventory.Inventory#contains(org.bukkit.inventory.ItemStack
		 * )
		 */
		@Override
		public boolean contains(ItemStack item) {
			return contains(item.getTypeId());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#contains(int, int)
		 */
		@Override
		public boolean contains(int materialId, int amount) {
			for (Inventory inventory : inventories) {
				if (inventory.contains(materialId, amount)) {
					return true;
				}
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#contains(org.bukkit.Material,
		 * int)
		 */
		@Override
		public boolean contains(Material material, int amount) {
			return contains(material.getId(), amount);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.bukkit.inventory.Inventory#contains(org.bukkit.inventory.ItemStack
		 * , int)
		 */
		@Override
		public boolean contains(ItemStack item, int amount) {
			return contains(item.getTypeId(), amount);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#all(int)
		 */
		@Override
		public HashMap<Integer, ? extends ItemStack> all(int materialId) {
			HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();

			for (Inventory inventory : inventories) {
				items.putAll(inventory.all(materialId));
			}

			return items;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#all(org.bukkit.Material)
		 */
		@Override
		public HashMap<Integer, ? extends ItemStack> all(Material material) {
			return all(material.getId());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.bukkit.inventory.Inventory#all(org.bukkit.inventory.ItemStack)
		 */
		@Override
		public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
			return all(item.getTypeId());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#first(int)
		 */
		@Override
		public int first(int materialId) {
			int pos = 0;
			for (Inventory inventory : inventories) {
				int first = inventory.first(materialId);
				if (first > -1) {
					return first;
				} else {
					pos += inventory.getSize();
				}
			}
			return pos == 0 ? -1 : pos;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#first(org.bukkit.Material)
		 */
		@Override
		public int first(Material material) {
			return first(material.getId());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.bukkit.inventory.Inventory#first(org.bukkit.inventory.ItemStack)
		 */
		@Override
		public int first(ItemStack item) {
			return first(item.getTypeId());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#firstEmpty()
		 */
		@Override
		public int firstEmpty() {
			for (Inventory inventory : inventories) {
				int firstEmpty = inventory.firstEmpty();
				if (firstEmpty > -1) {
					return firstEmpty;
				}
			}
			return -1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#remove(int)
		 */
		@Override
		public void remove(int materialId) {
			for (Inventory inventory : inventories) {
				HashMap<Integer, ? extends ItemStack> found = inventory.all(materialId);
				for (Integer slot : found.keySet()) {
					inventory.remove(slot);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#remove(org.bukkit.Material)
		 */
		@Override
		public void remove(Material material) {
			remove(material.getId());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.bukkit.inventory.Inventory#remove(org.bukkit.inventory.ItemStack)
		 */
		@Override
		public void remove(ItemStack item) {
			remove(item.getTypeId());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#clear(int)
		 */
		@Override
		public void clear(int index) {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#clear()
		 */
		@Override
		public void clear() {

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#getViewers()
		 */
		@Override
		public List<HumanEntity> getViewers() {
			List<HumanEntity> viewers = new ArrayList<HumanEntity>();
			for (Inventory inventory : inventories) {
				for (HumanEntity player : inventory.getViewers()) {
					if (!viewers.contains(player)) {
						viewers.add(player);
					}
				}
			}
			return viewers;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#getTitle()
		 */
		@Override
		public String getTitle() {
			return "WiperWorks: Combined Inventory";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#getType()
		 */
		@Override
		public InventoryType getType() {
			return InventoryType.CHEST;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#getHolder()
		 */
		@Override
		public InventoryHolder getHolder() {
			// TODO Auto-generated method stub
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#iterator()
		 */
		@Override
		public ListIterator<ItemStack> iterator() {
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			for (Inventory inventory : inventories) {
				items.addAll(Arrays.asList(inventory.getContents()));
			}
			return items.listIterator();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.bukkit.inventory.Inventory#iterator(int)
		 */
		@Override
		public ListIterator<ItemStack> iterator(int index) {
			return null;
		}
	}
}
