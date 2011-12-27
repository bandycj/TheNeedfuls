/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.model;

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
@Table(name = "ohnoez_enchants")
public class Enchant {

	@Id
	private Integer id;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "enchants")
	private Drop drop;

	@NotNull
	private Integer enchantId;

	@NotNull
	private Integer enchantLevel;

	public Enchant() {
		this(0, 0, null);
	}

	public Enchant(int enchantId, int enchantLevel, Drop drop) {
		this.enchantId = enchantId;
		this.enchantLevel = enchantLevel;
		this.drop = drop;
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
	public Drop getDrop() {
		return drop;
	}

	/**
	 * @param drop
	 *            the drop to set
	 */
	public void setDrop(Drop drop) {
		this.drop = drop;
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
