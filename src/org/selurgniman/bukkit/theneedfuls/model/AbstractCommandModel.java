/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.model;

import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Dec 27, 2011
 */
public class AbstractCommandModel implements ICommandModel {

	private TheNeedfuls plugin;
	
	/* (non-Javadoc)
	 * @see org.selurgniman.bukkit.theneedfuls.model.ICommandModel#setPlugin(org.selurgniman.bukkit.theneedfuls.TheNeedfuls)
	 */
	@Override
	public void setPlugin(TheNeedfuls plugin) {
		this.plugin = plugin;
	}

	/* (non-Javadoc)
	 * @see org.selurgniman.bukkit.theneedfuls.model.ICommandModel#getPlugin()
	 */
	@Override
	public TheNeedfuls getPlugin() {
		return this.plugin;
	}

}
