/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.listeners;

import org.bukkit.Material;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 29,
 *         2011
 */
public class TheNeedfulsInventoryListener extends InventoryListener {

	private TheNeedfuls plugin;

	public TheNeedfulsInventoryListener(TheNeedfuls plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onInventoryCraft(InventoryCraftEvent event) {
		if (!event.isCancelled() && event.getResult() != null) {
			if (event.getResult().getType() == Material.GLOWSTONE) {
				if (plugin.getModel().isCommandWorld(CommandType.TORCH, event.getPlayer().getWorld())) {
					event.setCancelled(true);
				}
			}
		}
	}
}
