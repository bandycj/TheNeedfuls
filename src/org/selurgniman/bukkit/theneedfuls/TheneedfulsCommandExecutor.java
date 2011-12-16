/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 14,
 *         2011
 */
public class TheneedfulsCommandExecutor implements CommandExecutor {

	private final Model model;

	public TheneedfulsCommandExecutor(Model model) {
		this.model = model;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length > 0) {

				ArrayList<String> messages = new ArrayList<String>();
				/*
				 * List items dropped from the last death
				 */
				if (args[0].toUpperCase().equals("LIST")) {
					
				}


				if (!messages.isEmpty()) {
					for (String message : messages) {
						player.sendMessage(message);
					}
					return true;
				}
			}
		}

		return false;
	}

}
