/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import com.google.common.collect.Maps;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 18, 2011
 */
public class Message {
	public static Message PREFIX = new Message("PREFIX", ChatColor.RED + "TheNeedfuls: " + ChatColor.WHITE);
	public static Message TORCH_AGE_MESSAGE = new Message("TORCH_AGE_MESSAGE", PREFIX
			+ "Torch expiration set to ("
			+ ChatColor.GREEN
			+ "%1$d seconds"
			+ ChatColor.WHITE
			+ ")");
	public static Message TORCH_REFRESH_MESSAGE = new Message("TORCH_REFRESH_MESSAGE", PREFIX
			+ "Torch refresh set to ("
			+ ChatColor.GREEN
			+ "%1$d seconds"
			+ ChatColor.WHITE
			+ ")");
	public static Message TORCH_COUNT_MESSAGE = new Message("TORCH_COUNT_MESSAGE", PREFIX
			+ "Torches being tracked ("
			+ ChatColor.GREEN
			+ "%1$d"
			+ ChatColor.WHITE
			+ ")");
	public static Message TORCH_EXPIRE_MESSAGE = new Message("TORCH_EXPIRE_MESSAGE", PREFIX
			+ "Torches expired ("
			+ ChatColor.RED
			+ "%1$d"
			+ ChatColor.WHITE
			+ ")");
	public static Message TORCH_WORLDS_MESSAGE = new Message("TORCH_WORLDS_MESSAGE", PREFIX
			+ "Worlds with expiring torches ("
			+ ChatColor.GREEN
			+ "%1$s"
			+ ChatColor.WHITE
			+ ")");
	public static Message ICE_QUANTITY_MESSAGE = new Message("ICE_QUANTITY_MESSAGE", PREFIX
			+ "Ice stack size dispensed ("
			+ ChatColor.BLUE
			+ "%1$d"
			+ ChatColor.WHITE
			+ ")");
	public static Message LACK_PERMISSION_MESSAGE = new Message("LACK_PERMISSION_MESSAGE", PREFIX
			+ "You are not an op or lack permission ("
			+ ChatColor.GREEN
			+ "%1$s.%2$s"
			+ ChatColor.WHITE
			+ ")");
	public static Message SHOW_EXPERIENCE_MESSAGE = new Message("SHOW_EXPERIENCE_MESSAGE", PREFIX
			+ "%1$s's level ("
			+ ChatColor.GREEN
			+ "%2$d"
			+ ChatColor.WHITE
			+ ")");
	public static Message LIST_ITEM_MESSAGE = new Message("LIST_ITEM_MESSAGE", PREFIX + "%1$s(" + ChatColor.GREEN + "%2$d" + ChatColor.WHITE + ")");
	public static Message NO_ITEMS_MESSAGE = new Message("NO_ITEMS_MESSAGE", PREFIX + "No dropped items to list!");
	public static Message AVAILABLE_CREDITS_MESSAGE = new Message("AVAILABLE_CREDITS_MESSAGE", PREFIX
			+ "Available credits for %1$s ("
			+ ChatColor.GREEN
			+ "%2$d"
			+ ChatColor.WHITE
			+ ").");
	public static Message CREDITED_BACK_MESSAGE = new Message("CREDITED_BACK_MESSAGE", PREFIX + "%1$s(" + ChatColor.GREEN + "%2$d" + ChatColor.WHITE + ")");
	public static Message CREDIT_USED_MESSAGE = new Message("CREDIT_USED_MESSAGE", PREFIX.toString()
			+ ChatColor.RED
			+ "%1$s"
			+ ChatColor.WHITE
			+ " just used an OhNoez credit for the day!");
	public static Message OTHER_PLAYER_DEATH_MESSAGE = new Message("OTHER_PLAYER_DEATH_MESSAGE", PREFIX.toString()
			+ ChatColor.AQUA
			+ "%1$s "
			+ ChatColor.WHITE
			+ "was just killed by "
			+ ChatColor.RED
			+ "%2$s"
			+ ChatColor.WHITE
			+ " %3$d blocks from you!");
	public static Message PLAYER_DEATH_MESSAGE = new Message("PLAYER_DEATH_MESSAGE", PREFIX.toString()
			+ ChatColor.AQUA
			+ "You"
			+ ChatColor.WHITE
			+ " died "
			+ ChatColor.RED
			+ "%1$s!");
	public static Message OHNOEZ_WORLDS_MESSAGE = new Message("OHNOEZ_WORLDS_MESSAGE", PREFIX
			+ "Worlds with OhNoez death recovery ("
			+ ChatColor.GREEN
			+ "%1$s"
			+ ChatColor.WHITE
			+ ")");

	private static Configuration config = null;
	private static Map<String, Message> values = Maps.newConcurrentMap();
	static {
		values.put(PREFIX.getKey(), PREFIX);
		values.put(TORCH_AGE_MESSAGE.getKey(), TORCH_AGE_MESSAGE);
		values.put(TORCH_REFRESH_MESSAGE.getKey(), TORCH_REFRESH_MESSAGE);
		values.put(TORCH_COUNT_MESSAGE.getKey(), TORCH_COUNT_MESSAGE);
		values.put(TORCH_EXPIRE_MESSAGE.getKey(), TORCH_EXPIRE_MESSAGE);
		values.put(TORCH_WORLDS_MESSAGE.getKey(), TORCH_WORLDS_MESSAGE);
		values.put(ICE_QUANTITY_MESSAGE.getKey(), ICE_QUANTITY_MESSAGE);
		values.put(LACK_PERMISSION_MESSAGE.getKey(), LACK_PERMISSION_MESSAGE);
		values.put(SHOW_EXPERIENCE_MESSAGE.getKey(), SHOW_EXPERIENCE_MESSAGE);
		values.put(PREFIX.getKey(), PREFIX);
		values.put(LIST_ITEM_MESSAGE.getKey(), LIST_ITEM_MESSAGE);
		values.put(NO_ITEMS_MESSAGE.getKey(), NO_ITEMS_MESSAGE);
		values.put(AVAILABLE_CREDITS_MESSAGE.getKey(), AVAILABLE_CREDITS_MESSAGE);
		values.put(CREDITED_BACK_MESSAGE.getKey(), CREDITED_BACK_MESSAGE);
		values.put(CREDIT_USED_MESSAGE.getKey(), CREDIT_USED_MESSAGE);
		values.put(OTHER_PLAYER_DEATH_MESSAGE.getKey(), OTHER_PLAYER_DEATH_MESSAGE);
		values.put(PLAYER_DEATH_MESSAGE.getKey(), PLAYER_DEATH_MESSAGE);
		values.put(OHNOEZ_WORLDS_MESSAGE.getKey(), OHNOEZ_WORLDS_MESSAGE);
	}

	private final String key;
	private final String message;

	private Message(String key, String message) {
		this.key = key;
		this.message = message;
	}

	public static void setConfig(Configuration config) {
		Message.config = config;
	}

	public String getKey() {
		return key;
	}

	public static Message valueOf(String key) {
		if (config != null) {
			String message = config.getString(key);
			if (message != null) {
				return new Message(key, message);
			}
		}
		Message message = values.get(key);
		if (message != null) {
			return message;
		}

		return new Message(key, key);
	}

	public static Set<Entry<String, Message>> values() {
		return values.entrySet();
	}

	@Override
	public String toString() {
		if (config != null) {
			String string = config.getString(key);
			if (string != null) {
				return string;
			}
		}

		return message;
	}
}
