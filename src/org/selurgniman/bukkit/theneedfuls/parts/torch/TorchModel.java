/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.torch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.selurgniman.bukkit.theneedfuls.model.AbstractCommandModel;
import org.selurgniman.bukkit.theneedfuls.model.dao.Torch;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 27,
 *         2011
 */
public class TorchModel extends AbstractCommandModel {

	public void removeTorch(Block torch) {
		Torch torchClass = getRecordForTorch(torch);
		if (torchClass != null) {
			removeTorch(torchClass);
		}
	}

	public void addTorch(Block torch) {
		if (getRecordForTorch(torch) == null) {
			createTorch(torch);
		}
	}

	public void expireAllTorches() {
		expireTorches(new Date());
	}

	public void expireTorches() {
		if (getPlugin() == null) return;
		
		Date maxAge = new Date((new Date().getTime() - (getPlugin().getConfig().getInt(Torch.TORCH_AGE_KEY) * 1000)));
		expireTorches(maxAge);
	}

	public void expireTorches(Date maxAge) {
		if (getPlugin() == null) return;
		
		for (Torch torch : getTorchesOlderThan(maxAge)) {
			World world = this.getPlugin().getServer().getWorld(UUID.fromString(torch.getWorldUuid()));
			Block torchBlock = world.getBlockAt(torch.getBlockX(), torch.getBlockY(), torch.getBlockZ());
			if (torchBlock.getType() == Material.TORCH) {
				torchBlock.setType(Material.AIR);
				world.dropItemNaturally(torchBlock.getLocation(), new ItemStack(Material.TORCH, 1));
				removeTorch(torch);
			}
		}
	}

	public int getTorchCount() {
		if (getPlugin() == null) return -1;
		
		return getPlugin().getDatabase().find(Torch.class).findRowCount();
	}

	private void removeTorch(Torch torch) {
		if (getPlugin() == null) return;
		
		getPlugin().getDatabase().delete(torch);
	}

	private Torch createTorch(Block torch) {
		if (getPlugin() == null) return null;
		
		Torch torchClass = new Torch();
		torchClass.setBlockX(torch.getX());
		torchClass.setBlockY(torch.getY());
		torchClass.setBlockZ(torch.getZ());
		torchClass.setWorldUuid(torch.getWorld().getUID().toString());
		torchClass.setPlacedDate(new Date());

		getPlugin().getDatabase().save(torchClass);
		return torchClass;
	}

	private Torch getRecordForTorch(Block torch) {
		if (getPlugin() == null) return null;
		
		Map<String, Object> location = new HashMap<String, Object>();
		location.put("blockX", String.valueOf(torch.getX()));
		location.put("blockY", String.valueOf(torch.getY()));
		location.put("blockZ", String.valueOf(torch.getZ()));
		location.put("worldUuid", torch.getWorld().getUID().toString());

		return getPlugin().getDatabase().find(Torch.class).where().allEq(location).findUnique();
	}

	private List<Torch> getTorchesOlderThan(Date date) {
		if (getPlugin() == null) return new ArrayList<Torch>();
		
		return getPlugin().getDatabase().find(Torch.class).where().lt("placed_date", date).findList();
	}
}
