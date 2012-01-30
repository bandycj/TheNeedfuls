///**
// * 
// */
//package org.selurgniman.bukkit.theneedfuls.parts.afk;
//
//import java.util.Comparator;
//import java.util.concurrent.ConcurrentSkipListMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import org.bukkit.entity.Player;
//import org.bukkit.event.player.PlayerChatEvent;
//import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.event.player.PlayerItemHeldEvent;
//import org.bukkit.event.player.PlayerListener;
//import org.bukkit.event.player.PlayerMoveEvent;
//import org.bukkit.event.player.PlayerToggleSneakEvent;
//import org.bukkit.event.player.PlayerToggleSprintEvent;
//import org.selurgniman.bukkit.theneedfuls.TheNeedfuls;
//
///**
// * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
// *         on: Jan 1, 2012
// */
//public class AfkPlayerListener extends PlayerListener {
//	private static ConcurrentSkipListMap<Player, Long> playersDoingThings = new ConcurrentSkipListMap<Player, Long>();
//
//	private final ExecutorService executor;
//
//	private static Long version = 0L;
//	private static int maxAge = 0;
//
//	public AfkPlayerListener(TheNeedfuls plugin) {
//		maxAge = plugin.getConfig().getInt(AfkCommand.CONFIG_AFK_DELAY);
//		Runnable versionTracker = new Runnable() {
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				version++;
//			}
//		};
//		executor = Executors.newSingleThreadExecutor();
//		executor.submit(versionTracker);
//		playersDoingThings = new ConcurrentSkipListMap<Player, Long>(new PlayerComparator());
//	}
//
//	@Override
//	public void onPlayerMove(PlayerMoveEvent event) {
//		if (!event.isCancelled() && !isPlayerActive(event.getPlayer())) {
//			event.setCancelled(true);
//			event.getPlayer().sendMessage("You're afk, you can't move!");
//		}
//	}
//
//	@Override
//	public void onPlayerInteract(PlayerInteractEvent event) {
//		if (!event.isCancelled()) {
//			setPlayerActive(event.getPlayer(), true);
//		}
//	}
//
//	@Override
//	public void onItemHeldChange(PlayerItemHeldEvent event) {
//		setPlayerActive(event.getPlayer(), true);
//	}
//
//	@Override
//	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
//		if (!event.isCancelled()) {
//			setPlayerActive(event.getPlayer(), true);
//		}
//	}
//
//	@Override
//	public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
//		if (!event.isCancelled()) {
//			setPlayerActive(event.getPlayer(), true);
//		}
//	}
//
//	@Override
//	public void onPlayerChat(PlayerChatEvent event) {
//		if (!event.isCancelled()) {
//			setPlayerActive(event.getPlayer(), true);
//		}
//	}
//
//	public static boolean isPlayerActive(Player player) {
//		if (!playersDoingThings.containsKey(player)) {
//			playersDoingThings.put(player, version);
//		}
//		return (version - playersDoingThings.get(player)) < maxAge;
//	}
//
//	public static void setPlayerActive(Player player, boolean active) {
//		if (active) {
//			playersDoingThings.put(player, version);
//		} else {
//			playersDoingThings.put(player, 0L);
//		}
//	}
//
//	public static void setDelay(int delay) {
//		maxAge = delay;
//	}
//
//	private class PlayerComparator implements Comparator<Player> {
//		@Override
//		public int compare(Player o1, Player o2) {
//			return o1.getName().compareTo(o2.getName());
//		}
//
//	}
//}
