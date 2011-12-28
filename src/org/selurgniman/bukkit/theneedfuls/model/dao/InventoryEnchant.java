/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.model.dao;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 23, 2011
 */
@Entity()
@Table(name = "theneedfuls_inventory_enchants")
public class InventoryEnchant {

	@Id
	private Integer id;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "enchants")
	private InventoryItem item;

	@NotNull
	private Integer enchantId;

	@NotNull
	private Integer enchantLevel;

	public InventoryEnchant() {
		this(0, 0, null);
	}

	public InventoryEnchant(int enchantId, int enchantLevel, InventoryItem item) {
		this.enchantId = enchantId;
		this.enchantLevel = enchantLevel;
		this.item = item;
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
	 * @return the drop
	 */
	public InventoryItem getItem() {
		return item;
	}

	/**
	 * @param drop
	 *            the drop to set
	 */
	public void setItem(InventoryItem item) {
		this.item = item;
	}

	/**
	 * @return the enchantId
	 */
	public Integer getEnchantId() {
		return enchantId;
	}

	/**
	 * @param enchantId
	 *            the enchantId to set
	 */
	public void setEnchantId(Integer enchantId) {
		this.enchantId = enchantId;
	}

	/**
	 * @return the enchantLevel
	 */
	public Integer getEnchantLevel() {
		return enchantLevel;
	}

	/**
	 * @param enchantLevel
	 *            the enchantLevel to set
	 */
	public void setEnchantLevel(Integer enchantLevel) {
		this.enchantLevel = enchantLevel;
	}

}
