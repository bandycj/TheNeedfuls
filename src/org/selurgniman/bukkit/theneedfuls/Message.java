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
	public static Message ICE_WORLDS_MESSAGE = new Message("ICE_WORLDS_MESSAGE", PREFIX
			+ "Ice stack size dispensed ("
			+ ChatColor.BLUE
			+ "%1$d"
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
