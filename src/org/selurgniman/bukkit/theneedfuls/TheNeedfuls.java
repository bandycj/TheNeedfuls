/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Category;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EndermanPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.selurgniman.bukkit.theneedfuls.commands.HelpCommand;
import org.selurgniman.bukkit.theneedfuls.commands.IceCommand;
import org.selurgniman.bukkit.theneedfuls.commands.TorchCommand;
import org.selurgniman.bukkit.theneedfuls.commands.XpCommand;
import org.selurgniman.bukkit.theneedfuls.listeners.TheNeedfulsBlockListener;
import org.selurgniman.bukkit.theneedfuls.listeners.TheNeedfulsEntityListener;
import org.selurgniman.bukkit.theneedfuls.model.Model;
import org.selurgniman.bukkit.theneedfuls.model.Torch;

import com.avaje.ebean.EbeanServer;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class TheNeedfuls extends JavaPlugin {
	private final Logger log = Logger.getLogger("Minecraft");
	private static final HashMap<String, Object> CONFIG_DEFAULTS = new HashMap<String, Object>();
	private Model model = null;
	private int torchTaskId = -1;

	static {
		CONFIG_DEFAULTS.put("iceMaker.quantity", 64);
		CONFIG_DEFAULTS.put(Torch.TORCH_REFRESH_KEY, 30);
		CONFIG_DEFAULTS.put(Torch.TORCH_AGE_KEY, 300);
		CONFIG_DEFAULTS.put(Torch.TORCH_WORLDS_KEY, Collections.EMPTY_LIST);
		for (Entry<String, Message> entry : Message.values()) {
			CONFIG_DEFAULTS.put(entry.getKey(), entry.getValue().toString());
		}
	}

	@Override
	public void onDisable() {
		this.saveConfig();
		log.info(Message.PREFIX + " disabled.");
	}

	@Override
	public void onEnable() {
		try {
			this.getDataFolder().createNewFile();
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage());
		}
		for (Entry<String,Object>entry:CONFIG_DEFAULTS.entrySet()){
			if(this.getConfig().get(entry.getKey()) == null){
				this.getConfig().set(entry.getKey(), entry.getValue());
			}
		}
		
		Message.setConfig(this.getConfig());

		this.model = new Model(this);
		setupDatabase();

		PluginManager pm = getServer().getPluginManager();
		TheNeedfulsEntityListener entityLlistener = new TheNeedfulsEntityListener(model);
		TheNeedfulsBlockListener blockLlistener = new TheNeedfulsBlockListener(this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockLlistener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockLlistener, Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_DISPENSE, blockLlistener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityLlistener, Priority.Highest, this);
		
		GenericPlayerListener genericPlayerListener =  new GenericPlayerListener();
		GenericEntityListener genericEntityListener = new GenericEntityListener();
		GenericBlockListener genericBlockListener = new GenericBlockListener();
		for (Event.Type type:Event.Type.values()){
			if (type.getCategory() == Category.BLOCK){
				pm.registerEvent(type,genericBlockListener, Priority.Normal, this);
			}
		}

		initTorchTask();

		this.getCommand("tnh").setExecutor(new HelpCommand());
		this.getCommand("tnt").setExecutor(new TorchCommand(this));
		this.getCommand("tni").setExecutor(new IceCommand(this));
		this.getCommand("tnx").setExecutor(new XpCommand(this));

		saveConfig();
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info(Message.PREFIX + pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	private class GenericPlayerListener extends PlayerListener {
		/**
	     * Called when a player joins a server
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerJoin(PlayerJoinEvent event) { System.out.println(event); }

	    /**
	     * Called when a player leaves a server
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerQuit(PlayerQuitEvent event) { System.out.println(event); }

	    /**
	     * Called when a player gets kicked from the server
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerKick(PlayerKickEvent event) { System.out.println(event); }

	    /**
	     * Called when a player sends a chat message
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerChat(PlayerChatEvent event) { System.out.println(event); }

	    /**
	     * Called early in the command handling process. This event is only
	     * for very exceptional cases and you should not normally use it.
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) { System.out.println(event); }

	    /**
	     * Called when a player attempts to move location in a world
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerMove(PlayerMoveEvent event) { System.out.println(event); }

	    /**
	     * Called before a player gets a velocity vector sent, which will "push"
	     * the player in a certain direction
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerVelocity(PlayerVelocityEvent event) { System.out.println(event); }

	    /**
	     * Called when a player attempts to teleport to a new location in a world
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerTeleport(PlayerTeleportEvent event) { System.out.println(event); }

	    /**
	     * Called when a player respawns
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerRespawn(PlayerRespawnEvent event) { System.out.println(event); }

	    /**
	     * Called when a player interacts with an object or air.
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerInteract(PlayerInteractEvent event) { System.out.println(event); }

	    /**
	     * Called when a player right clicks an entity.
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) { System.out.println(event); }

	    /**
	     * Called when a player attempts to log in to the server
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerLogin(PlayerLoginEvent event) { System.out.println(event); }

	    /**
	     * Called when a player has just been authenticated
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerPreLogin(PlayerPreLoginEvent event) { System.out.println(event); }

	    /**
	     * Called when a player throws an egg and it might hatch
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerEggThrow(PlayerEggThrowEvent event) { System.out.println(event); }

	    /**
	     * Called when a player plays an animation, such as an arm swing
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerAnimation(PlayerAnimationEvent event) { System.out.println(event); }

	    /**
	     * Called when a player opens an inventory
	     *
	     * @param event Relevant event details
	     */
	    public void onInventoryOpen(PlayerInventoryEvent event) { System.out.println(event); }

	    /**
	     * Called when a player changes their held item
	     *
	     * @param event Relevant event details
	     */
	    public void onItemHeldChange(PlayerItemHeldEvent event) { System.out.println(event); }

	    /**
	     * Called when a player drops an item from their inventory
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerDropItem(PlayerDropItemEvent event) { System.out.println(event); }

	    /**
	     * Called when a player picks an item up off the ground
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerPickupItem(PlayerPickupItemEvent event) { System.out.println(event); }

	    /**
	     * Called when a player toggles sneak mode
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) { System.out.println(event); }

	    /**
	     * Called when a player toggles sprint mode
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) { System.out.println(event); }

	    /**
	     * Called when a player fills a bucket
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerBucketFill(PlayerBucketFillEvent event) { System.out.println(event); }

	    /**
	     * Called when a player empties a bucket
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) { System.out.println(event); }

	    /**
	     * Called when a player enters a bed
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerBedEnter(PlayerBedEnterEvent event) { System.out.println(event); }

	    /**
	     * Called when a player leaves a bed
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerBedLeave(PlayerBedLeaveEvent event) { System.out.println(event); }

	    /**
	     * Called when a player is teleporting in a portal (after the animation)
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerPortal(PlayerPortalEvent event) { System.out.println(event); }

	    /**
	     * Called when a player is fishing
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerFish(PlayerFishEvent event) { System.out.println(event); }

	    /**
	     * Called when a player's game mode is changed
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) { System.out.println(event); }

	    /**
	     * Called after a player changes to a new world
	     *
	     * @param event Relevant event details
	     */
	    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) { System.out.println(event); }
	}
	private class GenericEntityListener extends EntityListener {
		/**
	     * Called when a creature is spawned into a world.
	     *<p />
	     * If a Creature Spawn event is cancelled, the creature will not spawn.
	     *
	     * @param event Relevant event details
	     */
	    public void onCreatureSpawn(CreatureSpawnEvent event) { System.out.println(event); }

	    /**
	     * Called when an item is spawned into a world
	     *
	     * @param event Relevant event details
	     */
	    public void onItemSpawn(ItemSpawnEvent event) { System.out.println(event); }

	    /**
	     * Called when an entity combusts.
	     *<p />
	     * If an Entity Combust event is cancelled, the entity will not combust.
	     *
	     * @param event Relevant event details
	     */
	    public void onEntityCombust(EntityCombustEvent event) { System.out.println(event); }

	    /**
	     * Called when an entity is damaged
	     *
	     * @param event Relevant event details
	     */
	    public void onEntityDamage(EntityDamageEvent event) { System.out.println(event); }

	    /**
	     * Called when an entity explodes
	     *
	     * @param event Relevant event details
	     */
	    public void onEntityExplode(EntityExplodeEvent event) { System.out.println(event); }

	    /**
	     * Called when an entity's fuse is lit
	     *
	     * @param event Relevant event details
	     */
	    public void onExplosionPrime(ExplosionPrimeEvent event) { System.out.println(event); }

	    /**
	     * Called when an entity dies
	     *
	     * @param event Relevant event details
	     */
	    public void onEntityDeath(EntityDeathEvent event) { System.out.println(event); }

	    /**
	     * Called when a creature targets another entity
	     *
	     * @param event Relevant event details
	     */
	    public void onEntityTarget(EntityTargetEvent event) { System.out.println(event); }

	    /**
	     * Called when an entity interacts with an object
	     *
	     * @param event Relevant event details
	     */
	    public void onEntityInteract(EntityInteractEvent event) { System.out.println(event); }

	    /**
	     * Called when an entity enters a portal
	     *
	     * @param event Relevant event details
	     */
	    public void onEntityPortalEnter(EntityPortalEnterEvent event) { System.out.println(event); }

	    /**
	     * Called when a painting is placed
	     *
	     * @param event Relevant event details
	     */
	    public void onPaintingPlace(PaintingPlaceEvent event) { System.out.println(event); }

	    /**
	     * Called when a painting is broken
	     *
	     * @param event Relevant event details
	     */
	    public void onPaintingBreak(PaintingBreakEvent event) { System.out.println(event); }

	    /**
	     * Called when a Pig is struck by lightning
	     *
	     * @param event Relevant event details
	     */
	    public void onPigZap(PigZapEvent event) { System.out.println(event); }

	    /**
	     * Called when a Creeper is struck by lightning.
	     *<p />
	     * If a Creeper Power event is cancelled, the Creeper will not be powered.
	     *
	     * @param event Relevant event details
	     */
	    public void onCreeperPower(CreeperPowerEvent event) { System.out.println(event); }

	    /**
	     * Called when an entity is tamed (currently only applies to Wolves)
	     *
	     * @param event Relevant event details
	     */
	    public void onEntityTame(EntityTameEvent event) { System.out.println(event); }

	    /**
	     * Called when an entity regains health (currently only applies to Players)
	     *
	     * @param event Relevant event details
	     */
	    public void onEntityRegainHealth(EntityRegainHealthEvent event) { System.out.println(event); }

	    /**
	     * Called when a project hits an object
	     *
	     * @param event Relevant event details
	     */
	    public void onProjectileHit(ProjectileHitEvent event) { System.out.println(event); }

	    /**
	     * Called when an Enderman picks a block up
	     *
	     * @param event Relevant event details
	     */
	    public void onEndermanPickup(EndermanPickupEvent event) { System.out.println(event); }

	    /**
	     * Called when an Enderman places a block
	     *
	     * @param event Relevant event details
	     */
	    public void onEndermanPlace(EndermanPlaceEvent event) { System.out.println(event); }

	    /**
	     * Called when a human entity's food level changes
	     *
	     * @param event Relevant event details
	     */
	    public void onFoodLevelChange(FoodLevelChangeEvent event) { System.out.println(event); }

	    /**
	     * Called when a Slime splits into smaller Slimes upon death
	     *
	     * @param event Relevant event details
	     */
	    public void onSlimeSplit(SlimeSplitEvent event) { System.out.println(event); }
	}
	private class GenericBlockListener extends BlockListener {
	    /**
	     * Called when a block is damaged by a player.
	     * <p />
	     * If a Block Damage event is cancelled, the block will not be damaged.
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockDamage(BlockDamageEvent event) { System.out.println(event); }

	    /**
	     * Called when we try to place a block, to see if we can build it here or not.
	     *<p />
	     * Note:
	     * <ul>
	     *    <li>The Block returned by getBlock() is the block we are trying to place on, not the block we are trying to place.</li>
	     *    <li>If you want to figure out what is being placed, use {@link BlockCanBuildEvent#getMaterial()} or {@link BlockCanBuildEvent#getMaterialId()} instead.</li>
	     * </ul>
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockCanBuild(BlockCanBuildEvent event) { System.out.println(event); }

	    /**
	     * Represents events with a source block and a destination block, currently only applies to liquid (lava and water).
	     *<p />
	     * If a Block From To event is cancelled, the block will not move (the liquid will not flow).
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockFromTo(BlockFromToEvent event) { System.out.println(event); }

	    /**
	     * Called when a block is ignited. If you want to catch when a Player places fire, you need to use {@link BlockPlaceEvent}.
	     *<p />
	     * If a Block Ignite event is cancelled, the block will not be ignited.
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockIgnite(BlockIgniteEvent event) { System.out.println(event); }

	    /**
	     * Called when block physics occurs.
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockPhysics(BlockPhysicsEvent event) { System.out.println(event); }

	    /**
	     * Called when a block is placed by a player.
	     *<p />
	     * If a Block Place event is cancelled, the block will not be placed.
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockPlace(BlockPlaceEvent event) { System.out.println(event); }

	    /**
	     * Called when redstone changes.<br />
	     * From: the source of the redstone change.<br />
	     * To: The redstone dust that changed.
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockRedstoneChange(BlockRedstoneEvent event) { System.out.println(event); }

	    /**
	     * Called when leaves are decaying naturally.
	     *<p />
	     * If a Leaves Decay event is cancelled, the leaves will not decay.
	     *
	     * @param event Relevant event details
	     */
	    public void onLeavesDecay(LeavesDecayEvent event) { System.out.println(event); }

	    /**
	     * Called when a sign is changed by a player.
	     * <p />
	     * If a Sign Change event is cancelled, the sign will not be changed.
	     *
	     * @param event Relevant event details
	     */
	    public void onSignChange(SignChangeEvent event) { System.out.println(event); }

	    /**
	     * Called when a block is destroyed as a result of being burnt by fire.
	     *<p />
	     * If a Block Burn event is cancelled, the block will not be destroyed as a result of being burnt by fire.
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockBurn(BlockBurnEvent event) { System.out.println(event); }

	    /**
	     * Called when a block is broken by a player.
	     *<p />
	     * Note:
	     * Plugins wanting to simulate a traditional block drop should set the block to air and utilise their own methods for determining
	     *   what the default drop for the block being broken is and what to do about it, if anything.
	     *<p />
	     * If a Block Break event is cancelled, the block will not break.
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockBreak(BlockBreakEvent event) { System.out.println(event); }

	    /**
	     * Called when a block is formed or spreads based on world conditions.
	     * Use {@link BlockSpreadEvent} to catch blocks that actually spread and don't just "randomly" form.
	     *<p />
	     * Examples:
	     *<ul>
	     *     <li>Snow forming due to a snow storm.</li>
	     *     <li>Ice forming in a snowy Biome like Tiga or Tundra.</li>
	     * </ul>
	     *<p />
	     * If a Block Form event is cancelled, the block will not be formed or will not spread.
	     *
	     * @see BlockSpreadEvent
	     * @param event Relevant event details
	     */
	    public void onBlockForm(BlockFormEvent event) { System.out.println(event); }

	    /**
	     * Called when a block spreads based on world conditions.
	     * Use {@link BlockFormEvent} to catch blocks that "randomly" form instead of actually spread.
	     *<p />
	     * Examples:
	     *<ul>
	     *     <li>Mushrooms spreading.</li>
	     *     <li>Fire spreading.</li>
	     * </ul>
	     *<p />
	     * If a Block Spread event is cancelled, the block will not spread.
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockSpread(BlockSpreadEvent event) { System.out.println(event); }

	    /**
	     * Called when a block fades, melts or disappears based on world conditions
	     * <p />
	     * Examples:
	     * <ul>
	     *     <li>Snow melting due to being near a light source.</li>
	     *     <li>Ice melting due to being near a light source.</li>
	     * </ul>
	     * <p />
	     * If a Block Fade event is cancelled, the block will not fade, melt or disappear.
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockFade(BlockFadeEvent event) { System.out.println(event); }

	    /**
	     * Called when an item is dispensed from a block.
	     *<p />
	     * If a Block Dispense event is cancelled, the block will not dispense the item.
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockDispense(BlockDispenseEvent event) { System.out.println(event); }

	    /**
	     * Called when a piston retracts
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockPistonRetract(BlockPistonRetractEvent event) { System.out.println(event); }

	    /**
	     * Called when a piston extends
	     *
	     * @param event Relevant event details
	     */
	    public void onBlockPistonExtend(BlockPistonExtendEvent event) { System.out.println(event); }
	}
	private void setupDatabase() {
		try {
			model.getDatabase().find(Torch.class).findRowCount();

		} catch (PersistenceException ex) {
			log.info("Installing database for " + getDescription().getName() + " due to first time usage");
			installDDL();
		}
	}

	public void initTorchTask() {
		if (-1 != torchTaskId) {
			this.getServer().getScheduler().cancelTask(torchTaskId);
		}

		torchTaskId = this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				model.expireTorches();
			}
		}, 60L, this.getConfig().getLong(Torch.TORCH_REFRESH_KEY) * 20);
	}

	public Model getModel() {
		return this.model;
	}

	@Override
	public EbeanServer getDatabase() {
		return model.getDatabase();
	}
}
