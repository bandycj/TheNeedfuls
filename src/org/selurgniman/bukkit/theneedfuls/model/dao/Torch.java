/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.model.dao;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 15, 2011
 */
@Entity()
@Table(name = "theneedfuls_torches")
public class Torch {
	public static final String TORCH_AGE_KEY = "torch.ageSeconds";
	public static final String TORCH_REFRESH_KEY = "torch.refreshFrequencySeconds";
	public static final String TORCH_WORLDS_KEY = "torch.worlds";

	@Id
	private int id;

	@NotNull
	private Integer blockX;

	@NotNull
	private Integer blockY;

	@NotNull
	private Integer blockZ;

	@NotEmpty
	private String worldUuid;

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
	 * @param blockX
	 *            the blockX to set
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
	 * @param blockY
	 *            the blockY to set
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
	 * @param blockZ
	 *            the blockZ to set
	 */
	public void setBlockZ(Integer blockZ) {
		this.blockZ = blockZ;
	}

	/**
	 * @return the worldName
	 */
	public String getWorldUuid() {
		return worldUuid;
	}

	/**
	 * @param worldName
	 *            the worldName to set
	 */
	public void setWorldUuid(String worldUuid) {
		this.worldUuid = worldUuid;
	}

	/**
	 * @return the placedDate
	 */
	public Date getPlacedDate() {
		return placedDate;
	}

	/**
	 * @param placedDate
	 *            the placedDate to set
	 */
	public void setPlacedDate(Date placedDate) {
		this.placedDate = placedDate;
	}

	@Override
	public String toString() {
		return blockX + ":" + blockY + ":" + blockZ + ":" + worldUuid;
	}
}
