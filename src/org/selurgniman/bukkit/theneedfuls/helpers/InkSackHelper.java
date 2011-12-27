/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.helpers;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a> Created
 *         on: Dec 24, 2011
 */
public enum InkSackHelper {
	INK_SACK(
			"Ink Sac",
			0),
	ROSE_RED(
			"Rose Red",
			1),
	CACTUS_GREEN(
			"Cactus Green",
			2),
	COCOA_BEANS(
			"Cocoa Beans",
			3),
	LAPIS_LAZULI(
			"Lapis Lazuli",
			4),
	PURPLE_DYE(
			"Purple Dye",
			5),
	CYAN_DYE(
			"Cyan Dye",
			6),
	LIGHT_GRAY_DYE(
			"Light Gray Dye",
			7),
	GRAY_DYE(
			"Gray Dye",
			8),
	PINK_DYER(
			"Pink Dye",
			9),
	LIME_DYE(
			"Lime Dye",
			10),
	DANDELION_YELLOW(
			"Dandelion Yellow",
			11),
	LIGHT_BLUE_DYE(
			"Light Blue Dye",
			12),
	MAGENTA_DYE(
			"Magenta Dye",
			13),
	ORANGE_DYE(
			"Orange Dye",
			14),
	BONE_MEAL(
			"Bone Meal",
			15);

	private final String name;
	private final int durability;

	private InkSackHelper(String name, int durability) {
		this.name = name;
		this.durability = durability;
	}
	
	public static InkSackHelper getType(int durability){
		return InkSackHelper.values()[durability];
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the durability
	 */
	public int getDurability() {
		return durability;
	}
}
