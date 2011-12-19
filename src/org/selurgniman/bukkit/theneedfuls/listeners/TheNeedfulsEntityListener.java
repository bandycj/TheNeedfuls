/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;
import org.selurgniman.bukkit.theneedfuls.model.Model;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 17, 2011
 */
public class TheNeedfulsEntityListener extends EntityListener {
	private final Model model;

	public TheNeedfulsEntityListener(Model model) {
		this.model = model;
	}

	@Override
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!event.isCancelled() && event.blockList().size() > 0 && model.isTorchWorld(event.blockList().get(0).getWorld())) {
			for (Block block : event.blockList()) {
				if (block.getType() == Material.TORCH) {
					model.removeTorch(block);
				}
			}
		}
	}
}
