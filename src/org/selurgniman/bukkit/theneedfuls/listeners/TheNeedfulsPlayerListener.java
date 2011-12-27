/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.listeners;

import java.util.Date;
import java.util.IdentityHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
import org.selurgniman.bukkit.theneedfuls.commands.WorldsCommand;
import org.selurgniman.bukkit.theneedfuls.helpers.Message;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 26, 2011
 */
public class TheNeedfulsPlayerListener extends PlayerListener {
	private static final BlockFace[] blockfaces = new BlockFace[] { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.EAST,
			BlockFace.WEST, BlockFace.SOUTH, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST };
	private final TheNeedfuls plugin;
	private final Pattern signPattern = Pattern.compile("^\\[(.*)\\]$");
	private final IdentityHashMap<Player, Date> playersTeleported = new IdentityHashMap<Player, Date>();

	public TheNeedfulsPlayerListener(TheNeedfuls plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		Block clickedBlock = event.getClickedBlock();
		if (clickedBlock != null && clickedBlock.getType() == Material.STONE_PLATE) {
			String worldName = "undefined";
			Player player = event.getPlayer();
			Block nextLevel = clickedBlock.getRelative(BlockFace.DOWN);
			BlockFace airBlock = null;

			boolean valid = (nextLevel.getType() == Material.IRON_BLOCK && clickedBlock.getRelative(BlockFace.UP, 2).getType() == Material.GLOWSTONE);

			if (valid && playersTeleported.get(player) != null) {
				Date now = new Date();
				now.setTime(now.getTime() - (plugin.getConfig().getLong(WorldsCommand.CONFIG_TELEPORT_DELAY) * 1000));
				if (playersTeleported.get(player).after(now)) {
					Long timeRemaining = (playersTeleported.get(player).getTime() - now.getTime()) / 1000;
					player.sendMessage(String.format(Message.WORLD_DELAY_MESSAGE.toString(), timeRemaining));
					valid = false;
				}
			}

			if (valid) {
				for (BlockFace blockface : blockfaces) {
					if (nextLevel.getRelative(blockface).getType() != Material.IRON_BLOCK) {
						valid = false;
						break;
					}
				}
			}

			for (int i = 0; i < 2; i++) {
				if (valid) {
					nextLevel = nextLevel.getRelative(BlockFace.UP);
					for (BlockFace blockface : blockfaces) {
						Block nextBlock = nextLevel.getRelative(blockface);
						if (blockface == BlockFace.NORTH || blockface == BlockFace.SOUTH || blockface == BlockFace.EAST || blockface == BlockFace.WEST) {
							if (nextBlock.getType() == Material.AIR && airBlock == null) {
								airBlock = blockface;
							} else if (nextBlock.getType() == Material.AIR && blockface != airBlock) {
								valid = false;
								break;
							} else if (nextBlock.getType() != Material.GLASS && blockface != airBlock) {
								valid = false;
								break;
							}
						} else if ((blockface == BlockFace.NORTH_EAST || blockface == BlockFace.NORTH_WEST || blockface == BlockFace.SOUTH_EAST || blockface == BlockFace.SOUTH_WEST)
								&& nextBlock.getType() != Material.IRON_BLOCK) {
							valid = false;
							break;
						}
					}
				} else {
					break;
				}
			}

			if (valid) {
				nextLevel = nextLevel.getRelative(BlockFace.UP);
				for (BlockFace blockface : blockfaces) {
					Block nextBlock = nextLevel.getRelative(blockface);
					if (nextBlock.getType() != Material.IRON_BLOCK) {
						valid = false;
						break;
					}
				}
			}

			if (valid) {
				Block signBlock = nextLevel.getRelative(airBlock, 2);
				if (signBlock.getState() instanceof Sign) {
					for (String line : ((Sign) signBlock.getState()).getLines()) {
						Matcher matcher = signPattern.matcher(line);
						if (matcher.matches()) {
							worldName = matcher.group(1);
						}
					}
				}
			}

			if (valid && !worldName.isEmpty()) {
				World world = plugin.getServer().getWorld(worldName);
				if (world != null && world.getEnvironment() == player.getWorld().getEnvironment()) {
					player.teleport(world.getSpawnLocation(), TeleportCause.PLUGIN);
					player.sendMessage(String.format(Message.WORLD_TELEPORT_MESSAGE.toString(), worldName));
					playersTeleported.put(player, new Date());
					event.setCancelled(true);
				} else {
					player.sendMessage(String.format(Message.WORLD_UNKNOWN_MESSAGE.toString(), worldName));
				}
			}
		}
	}

	@Override
	public void onPlayerPortal(PlayerPortalEvent event) {
		if (!event.isCancelled() && event.getTo() == null) {
			World currentWorld = event.getFrom().getWorld();
			double x = 0.0;
			double y = 0.0;
			double z = 0.0;
			String toWorldName = "";
			Environment toEnvironment = null;
			switch (currentWorld.getEnvironment()) {
				case NETHER: {
					toEnvironment = Environment.NORMAL;
					toWorldName = currentWorld.getName().toLowerCase().replace("_nether", "");
					x = event.getFrom().getX() * 8;
					y = event.getFrom().getY() * 8;
					z = event.getFrom().getZ() * 8;
					break;
				}
				case NORMAL: {
					toEnvironment = Environment.NETHER;
					toWorldName = currentWorld.getName() + "_nether";
					x = event.getFrom().getX() / 8;
					y = event.getFrom().getY() / 8;
					z = event.getFrom().getZ() / 8;
				}
			}

			World toWorld = plugin.getServer().getWorld(toWorldName);
			if (toWorld == null) {
				WorldCreator creator = new WorldCreator(toWorldName);
				creator.environment(toEnvironment);
				toWorld = plugin.getServer().createWorld(creator);
			}
			Location toLocation = new Location(toWorld, x, y, z);
			event.getPortalTravelAgent().findOrCreate(toLocation);
			event.useTravelAgent(true);
			event.setTo(toLocation);
			checkInventory(event);
		}
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (!event.isCancelled()) {
			checkInventory(event);
		}
	}
	
	private void checkInventory(PlayerTeleportEvent event){
		World from = event.getFrom().getWorld();
		World to = event.getTo().getWorld();
		if (from != to) {
			String fromString = from.getName().toLowerCase().replace("_nether", "").replace("_the_end", "");
			String toString = to.getName().toLowerCase().replace("_nether", "").replace("_the_end", "");
			if (!fromString.equals(toString)) {
				System.out.println("player:"+event.getPlayer());
				plugin.getModel().storeWorldInventory(from, event.getPlayer());
				plugin.getModel().restoreWorldInventory(to, event.getPlayer());
			}
		}
	}
}
