/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Dec 15, 2011
 */
@Entity()
@Table(name = "theneedfuls_torches")
public class Torch {
    @Id
    private int id;
        
	@NotNull
    private Integer blockX;
    
    @NotNull
    private Integer blockY;
    
    @NotNull
    private Integer blockZ;
    
    @NotNull
    private Date placedDate;


	public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
	 * @return the blockX
	 */
	public Integer getBlockX() {
		return blockX;
	}

	/**
	 * @param blockX the blockX to set
	 */
	public void setBlockX(Integer blockX) {
		this.blockX = blockX;
	}

	/**
	 * @return the blockY
	 */
	public Integer getBlockY() {
		return blockY;
	}

	/**
	 * @param blockY the blockY to set
	 */
	public void setBlockY(Integer blockY) {
		this.blockY = blockY;
	}

	/**
	 * @return the blockZ
	 */
	public Integer getBlockZ() {
		return blockZ;
	}

	/**
	 * @param blockZ the blockZ to set
	 */
	public void setBlockZ(Integer blockZ) {
		this.blockZ = blockZ;
	}
    
    /**
	 * @return the placedDate
	 */
	public Date getPlacedDate() {
		return placedDate;
	}

	/**
	 * @param placedDate the placedDate to set
	 */
	public void setPlacedDate(Date placedDate) {
		this.placedDate = placedDate;
	}
}
