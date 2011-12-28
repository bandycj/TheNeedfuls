/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.model.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 14,
 *         2011
 */
@Entity()
@Table(name = "theneedfuls_inventory_items")
public class InventoryItem {

	@Id
	private Integer id;

	@NotEmpty
	private String player;
	
	@NotNull
	private Integer itemId;

	@NotNull
	private Integer itemCount;

	@NotNull
	private Integer itemData;

	@NotNull
	private Integer itemDurability;
	
	@NotNull
	private Integer itemSlot;

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
	private List<InventoryEnchant> enchants;

	@NotEmpty
	private String worldUuid;

	public InventoryItem() {
		this("",0, 0, 0, 0, -99, new ArrayList<InventoryEnchant>(), "");
	}

	public InventoryItem(String player,int itemId, int itemCount, int itemData, int itemDurability, int itemSlot, String worldUuid) {
		this(player,itemId, itemCount, itemData, itemDurability, itemSlot, new ArrayList<InventoryEnchant>(), worldUuid);
	}

	public InventoryItem(String player,int itemId, int itemCount, int itemData, int itemDurability, int itemSlot, List<InventoryEnchant> enchants, String worldUuid) {
		this.player = player;
		this.itemId = itemId;
		this.itemCount = itemCount;
		this.itemData = itemData;
		this.itemDurability = itemDurability;
		this.itemSlot = itemSlot;
		this.enchants = enchants;
		this.worldUuid = worldUuid;
	}

	/**
	 * @return the player
	 */
	public String getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(String player) {
		this.player = player;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the itemId
	 */
	public Integer getItemId() {
		return itemId;
	}

	/**
	 * @param itemId
	 *            the itemId to set
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the itemCount
	 */
	public Integer getItemCount() {
		return itemCount;
	}

	/**
	 * @param itemCount
	 *            the itemCount to set
	 */
	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	/**
	 * @return the itemData
	 */
	public Integer getItemData() {
		return itemData;
	}

	/**
	 * @param itemData
	 *            the itemData to set
	 */
	public void setItemData(Integer itemData) {
		this.itemData = itemData;
	}

	/**
	 * @return the itemDurability
	 */
	public Integer getItemDurability() {
		return itemDurability;
	}

	/**
	 * @param itemDurability
	 *            the itemDurability to set
	 */
	public void setItemDurability(Integer itemDurability) {
		this.itemDurability = itemDurability;
	}

	/**
	 * @return the worldUuid
	 */
	public String getWorldUuid() {
		return worldUuid;
	}

	/**
	 * @param worldUuid
	 *            the worldUuid to set
	 */
	public void setWorldUuid(String worldUuid) {
		this.worldUuid = worldUuid;
	}

	/**
	 * @return the enchants
	 */
	public List<InventoryEnchant> getEnchants() {
		return enchants;
	}

	/**
	 * @param enchants
	 *            the enchants to set
	 */
	public void setEnchants(List<InventoryEnchant> enchants) {
		this.enchants = enchants;
	}

	/**
	 * @return the itemSlot
	 */
	public Integer getItemSlot() {
		return itemSlot;
	}

	/**
	 * @param itemSlot the itemSlot to set
	 */
	public void setItemSlot(Integer itemSlot) {
		this.itemSlot = itemSlot;
	}
	
}
