package org.selurgniman.bukkit.theneedfuls;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.selurgniman.bukkit.theneedfuls.helpers.Message;
import org.selurgniman.bukkit.theneedfuls.model.dao.Credit;
import org.selurgniman.bukkit.theneedfuls.model.dao.Torch;
import org.selurgniman.bukkit.theneedfuls.parts.ice.IceCommand;
import org.selurgniman.bukkit.theneedfuls.parts.misc.SortCommand;
import org.selurgniman.bukkit.theneedfuls.parts.worlds.WorldsCommand;
/**
 * 
 */

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 *
 */
public class ConfigUtils {
	private static final LinkedHashMap<String, Object> CONFIG_DEFAULTS = new LinkedHashMap<String, Object>();

	public static final String AFK_ENABLED = "afk.enabled";
	public static final String ENCHANTING_REPAIR_ENABLED = "enchantingrepair.enabled";
	public static final String ICE_ENABLED = "icecommand.enabled";
	public static final String COMBINED_INVENTORY_ENABLED = "combinedinventory.enabled";
	public static final String DEBUG_ENABLED = "debugcommand.enabled";
	public static final String DROP_ENHANCER_ENABLED = "dropenhancer.enabled";
	public static final String SIGN_PLACER_ENABLED = "signplacer.enabled";
	public static final String SORT_ENABLED = "sortcommand.enabled";
	public static final String XP_ENABLED = "xpcommand.enabled";
	public static final String OHNOEZ_ENABLED = "ohnoez.enabled";
	public static final String TORCH_ENABLED = "torchlistener.enabled";
	public static final String WEATHER_ENABLED = "weathercontrol.enabled";
	public static final String WORLDS_ENABLED = "worldscontrol.enabled";
	
	
	static {
		// ;AfkCommand.CONFIG_AFK_DELAY, 300);
		CONFIG_DEFAULTS.put(AFK_ENABLED, false);
		CONFIG_DEFAULTS.put(ENCHANTING_REPAIR_ENABLED, false);
		CONFIG_DEFAULTS.put(ICE_ENABLED, false);
		CONFIG_DEFAULTS.put(COMBINED_INVENTORY_ENABLED, false);
		CONFIG_DEFAULTS.put(DEBUG_ENABLED, false);
		CONFIG_DEFAULTS.put(DROP_ENHANCER_ENABLED, false);
		CONFIG_DEFAULTS.put(SIGN_PLACER_ENABLED, false);
		CONFIG_DEFAULTS.put(SORT_ENABLED, false);
		CONFIG_DEFAULTS.put(XP_ENABLED, false);
		CONFIG_DEFAULTS.put(OHNOEZ_ENABLED, false);
		CONFIG_DEFAULTS.put(TORCH_ENABLED, false);
		CONFIG_DEFAULTS.put(WEATHER_ENABLED, false);
		CONFIG_DEFAULTS.put(WORLDS_ENABLED, false);
		CONFIG_DEFAULTS.put(SortCommand.CONFIG_SORT_DISTANCE, 5);
		CONFIG_DEFAULTS.put(IceCommand.CONFIG_ICE_QUANTITY, 64);
		CONFIG_DEFAULTS.put(WorldsCommand.CONFIG_TELEPORT_DELAY, 300);
		CONFIG_DEFAULTS.put(Credit.OHNOEZ_WORLDS_KEY, Collections.EMPTY_LIST);
		CONFIG_DEFAULTS.put(Torch.TORCH_REFRESH_KEY, 30);
		CONFIG_DEFAULTS.put(Torch.TORCH_AGE_KEY, 300);
		CONFIG_DEFAULTS.put(Torch.TORCH_WORLDS_KEY, Collections.EMPTY_LIST);
		CONFIG_DEFAULTS.put("Debug", false);
		CONFIG_DEFAULTS.put("DebugPlayer", false);
		for (Entry<String, Message> entry : Message.values()) {
			CONFIG_DEFAULTS.put(entry.getKey(), entry.getValue().toString());
		}
	}
	public static Map<String, Object> getDefaults(){
		return 	CONFIG_DEFAULTS;
	}
}
