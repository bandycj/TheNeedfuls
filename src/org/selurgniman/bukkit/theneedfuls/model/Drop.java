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
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Dec 14, 2011
 */
@Entity()
@Table(name = "ohnoez_drops")
public class Drop {

	@Id
	private Integer id;

	@ManyToOne(cascade=CascadeType.REFRESH)
	@JoinColumn(name="drops")
	private Credit credit;
	
	@NotNull
    private Integer itemId;
    
    @NotNull
    private Integer itemCount;
    
    @NotNull
    private Integer itemData;
    
    @NotNull
    private Integer itemDurability;

	public Drop(){
    	this(0,0);
    }
    
    public Drop(int itemId, int itemCount){
    	this.itemId=itemId;
    	this.itemCount=itemCount;
    }
	
    /**
	 * @return the credit
	 */
	public Credit getCredit() {
		return credit;
	}

	/**
	 * @param credit the credit to set
	 */
	public void setCredit(Credit credit) {
		this.credit = credit;
	}

	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
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
	 * @param itemId the itemId to set
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
	 * @param itemCount the itemCount to set
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
	 * @param itemData the itemData to set
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
	 * @param itemDurability the itemDurability to set
	 */
	public void setItemDurability(Integer itemDurability) {
		this.itemDurability = itemDurability;
	}
}
