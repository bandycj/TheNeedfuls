/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.parts.worlds;

import java.util.Date;
import java.util.IdentityHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Effect;
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
import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Model.CommandType;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 26, 2011
 */
public class WorldsPlayerListener extends PlayerListener {
	private final TheNeedfuls plugin;
	private final WorldsModel worldsModel;
	private final Pattern signPattern = Pattern.compile("^\\[(.*)\\]$");
	private final IdentityHashMap<Player, Date> playersTeleported = new IdentityHashMap<Player, Date>();

	public WorldsPlayerListener(TheNeedfuls plugin) {
		this.plugin = plugin;
		this.worldsModel = (WorldsModel) Model.getCommandModel(CommandType.WORLDS);
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.isCancelled()) {
			Block clickedBlock = event.getClickedBlock();
			if (clickedBlock != null && clickedBlock.getType() == Material.STONE_PLATE) {
				Player player = event.getPlayer();
				
				TeleporterValidator validator = new TeleporterValidator(player,clickedBlock);
				if (validator.isValid()) {
					clickedBlock.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 4);
					String worldName = validator.getWorldName();
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

	private void checkInventory(PlayerTeleportEvent event) {
		World from = event.getFrom().getWorld();
		World to = event.getTo().getWorld();
		if (from != to) {
			String fromString = from.getName().toLowerCase().replace("_nether", "").replace("_the_end", "");
			String toString = to.getName().toLowerCase().replace("_nether", "").replace("_the_end", "");
			if (!fromString.equals(toString)) {
				worldsModel.storeWorldInventory(from, event.getPlayer());
				worldsModel.restoreWorldInventory(to, event.getPlayer());
			}
		}
	}
	
	private class TeleporterValidator {
		private BlockFace airBlockFace = null;
		private String worldName = "";
		private boolean valid;
		
		public TeleporterValidator(Player player, Block clickedBlock){
			Block nextLevel = clickedBlock.getRelative(BlockFace.DOWN);

			valid = (nextLevel.getType() == Material.IRON_BLOCK && clickedBlock.getRelative(BlockFace.UP, 2).getType() == Material.GLOWSTONE);

			if (valid) {
				valid = isDelayMet(player);
			}

			if (valid) {
				valid = isBaseCorrect(nextLevel);
			}

			for (int i = 0; i < 2; i++) {
				if (valid) {
					nextLevel = nextLevel.getRelative(BlockFace.UP);
					valid = isSideCorrect(nextLevel);
				} else {
					break;
				}
			}

			if (valid) {
				nextLevel = nextLevel.getRelative(BlockFace.UP);
				valid = isBaseCorrect(nextLevel);
			}

			if (valid) {
				worldName = getWorldName(nextLevel, airBlockFace);
				valid = worldName.isEmpty() == false;
			}
		}
		
		private boolean isDelayMet(Player player) {
			Date now = new Date();
			now.setTime(now.getTime() - (plugin.getConfig().getLong(WorldsCommand.CONFIG_TELEPORT_DELAY) * 1000));
			if (playersTeleported.get(player) == null) {
				playersTeleported.put(player, now);
				return true;
			}

			if (playersTeleported.get(player).after(now)) {
				Long timeRemaining = (playersTeleported.get(player).getTime() - now.getTime()) / 1000;
				player.sendMessage(String.format(Message.WORLD_DELAY_MESSAGE.toString(), timeRemaining));
				return false;
			}
			return true;
		}
		
		private boolean isBaseCorrect(Block base) {
			for (BlockFace blockface : TheNeedfuls.BLOCKFACES) {
				if (base.getRelative(blockface).getType() != Material.IRON_BLOCK) {
					return false;
				}
			}
			return true;
		}

		private boolean isSideCorrect(Block side) {
			for (BlockFace blockface : TheNeedfuls.BLOCKFACES) {
				Block nextBlock = side.getRelative(blockface);
				if (blockface == BlockFace.NORTH || blockface == BlockFace.SOUTH || blockface == BlockFace.EAST || blockface == BlockFace.WEST) {
					if (nextBlock.getType() == Material.AIR && airBlockFace == null) {
						airBlockFace = blockface;
					} else if (nextBlock.getType() == Material.AIR && blockface != airBlockFace) {
						return false;
					} else if (nextBlock.getType() != Material.GLASS && blockface != airBlockFace) {
						return false;
					}
				} else if ((blockface == BlockFace.NORTH_EAST || blockface == BlockFace.NORTH_WEST || blockface == BlockFace.SOUTH_EAST || blockface == BlockFace.SOUTH_WEST)
						&& nextBlock.getType() != Material.IRON_BLOCK) {
					return false;
				}
			}

			return true;
		}

		private String getWorldName(Block topBlock, BlockFace airBlockFace) {
			Block signBlock = topBlock.getRelative(airBlockFace, 2);
			if (signBlock.getState() instanceof Sign) {
				for (String line : ((Sign) signBlock.getState()).getLines()) {
					Matcher matcher = signPattern.matcher(line);
					if (matcher.matches()) {
						return matcher.group(1);
					}
				}
			}
			return "";
		}
		
		public String getWorldName(){
			return this.worldName;
		}
		
		public boolean isValid(){
			return this.valid;
		}
	}
}
