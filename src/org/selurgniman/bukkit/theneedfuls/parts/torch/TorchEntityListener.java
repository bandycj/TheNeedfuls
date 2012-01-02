/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.torch;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 30, 2011
 */
public class TorchEntityListener extends EntityListener {

	private final TheNeedfuls plugin;
	private final TorchModel torchModel;
	
	public TorchEntityListener(TheNeedfuls plugin) {
		this.plugin = plugin;
		this.torchModel = (TorchModel) Model.getCommandModel(CommandType.TORCH);

	}
	
	@Override
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!event.isCancelled() && event.blockList().size() > 0 && plugin.getModel().isCommandWorld(CommandType.TORCH, event.blockList().get(0).getWorld())) {
			for (Block block : event.blockList()) {
				if (block.getType() == Material.TORCH) {
					torchModel.removeTorch(block);
				}
			}
		}
	}
}
