/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.listeners;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;
import org.selurgniman.bukkit.theneedfuls.model.TorchModel;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 14, 2011
 */
public class TheNeedfulsBlockListener extends BlockListener {
	private final TheNeedfuls plugin;
	private final TorchModel torchModel;

	public TheNeedfulsBlockListener(TheNeedfuls plugin) {
		this.plugin = plugin;
		this.torchModel = (TorchModel) Model.getCommandModel(CommandType.TORCH);

	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.isCancelled()) {
			Block placedBlock = event.getBlockPlaced();
			if (placedBlock.getType() == Material.TORCH && plugin.getModel().isCommandWorld(CommandType.TORCH, placedBlock.getWorld())) {
				torchModel.addTorch(event.getBlock());
			}
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			Block block = event.getBlock();
			if (block.getType() == Material.TORCH && plugin.getModel().isCommandWorld(CommandType.TORCH, block.getWorld())) {
				torchModel.removeTorch(event.getBlock());
			} else if (block.getType() == Material.GLASS || block.getType() == Material.THIN_GLASS) {
				Random r = new Random();
				int glassChance = r.nextInt(3);
				int sandChance = r.nextInt(2);

				if (glassChance > 0) {
					block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType(), 1));
				} else if (sandChance > 0 && block.getType() == Material.GLASS) {
					block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SAND, 1));
				}
			} else if (block.getType() == Material.LEAVES){
				Random r = new Random();
				int appleChance = r.nextInt(10);
				if (appleChance>8){
					block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.APPLE,1));
				}
			}
		}
	}

	@Override
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (!event.isCancelled()) {
			Block block = event.getBlock();
			if (event.getCause() == IgniteCause.FLINT_AND_STEEL && plugin.getModel().isCommandWorld(CommandType.TORCH, block.getWorld())) {
				if (block.getRelative(BlockFace.DOWN).getType() == Material.NETHERRACK) {
					event.setCancelled(true);
				}
			}
		}
	}

	@Override
	public void onBlockDispense(BlockDispenseEvent event) {
		if (!event.isCancelled() && event.getItem().getType() == Material.WATER_BUCKET) {
			Block block = event.getBlock();
			if (block.getRelative(BlockFace.DOWN).getType() == Material.SNOW_BLOCK) {
				event.setItem(new ItemStack(Material.ICE, plugin.getConfig().getInt("iceMaker.quantity")));
				block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.BUCKET, 1));
			}
		}
	}

	@Override
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {

		if (!event.isCancelled() && event.getDirection() == BlockFace.UP) {
			Block piston = event.getBlock();

			Block aboveBlock = piston.getRelative(BlockFace.UP, 2);
			Block aboveBlock2 = piston.getRelative(BlockFace.UP, 3);
			Block aboveBlock3 = piston.getRelative(BlockFace.UP, 4);

			boolean valid = (aboveBlock.getType() == Material.IRON_FENCE && aboveBlock2.getType() == Material.IRON_FENCE && aboveBlock3.getType() == Material.IRON_FENCE);
			if (valid) {
				for (BlockFace blockFace : TheNeedfuls.BLOCKFACES) {
					if (aboveBlock3.getRelative(blockFace).getType() != Material.IRON_FENCE) {
						System.out.println(aboveBlock3.getRelative(blockFace).getType());
						valid = false;
						break;
					}
				}
			}
			System.out.println("valid: "+valid);
			if (valid) {
				piston.getWorld().strikeLightningEffect(aboveBlock3.getLocation());
				piston.getWorld().setWeatherDuration(0);
				piston.getWorld().setStorm(false);
			}
		}
	}
}