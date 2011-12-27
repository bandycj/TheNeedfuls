/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.helpers;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 22,
 *         2011
 */
public enum PotionHelper {

	WATER_BOTTLE(
			"Water Bottle",
			-1,
			0,
			"Starting point for every recipe.",
			PotionCategory.NEUTRAL),
	AWKWARD(
			"Awkward Potion",
			-1,
			16,
			"Base for all primary potions (and by extension, splash potions).",
			PotionCategory.NEUTRAL),
	THICK(
			"Thick Potion",
			-1,
			32,
			"Base for Potion of Weakness.",
			PotionCategory.NEUTRAL),
	MUNDANE_EX(
			"Mundane Potion (extended)",
			-1,
			64,
			"Base for Potion of Weakness (extended)",
			PotionCategory.NEUTRAL),
	MUNDANE(
			"Mundane Potion",
			-1,
			8192,
			"Base for Potion of Weakness",
			PotionCategory.NEUTRAL),
	// Potions with Positive Effects
	REGENERATION(
			"Potion of Regeneration",
			45,
			8193,
			"Restores health over time.",
			PotionCategory.POSITIVE),
	REGENERATION_I(
			"Potion of Regeneration",
			120,
			8257,
			"Restores health over time.",
			PotionCategory.POSITIVE),
	REGENERATION_II(
			"Potion of Regeneration II",
			22,
			8225,
			"Restores health over time.",
			PotionCategory.POSITIVE),
	SWIFTNESS(
			"Potion of Swiftness",
			180,
			8194,
			"Increases player's movement, sprinting speed, and jumping length.",
			PotionCategory.POSITIVE),
	SWIFTNESS_I(
			"Potion of Swiftness",
			480,
			8258,
			"Increases player's movement, sprinting speed, and jumping length.",
			PotionCategory.POSITIVE),
	SWIFTNESS_II(
			"Potion of Swiftness II",
			90,
			8226,
			"Increases player's movement, sprinting speed, and jumping length.",
			PotionCategory.POSITIVE),
	FIRE(
			"Potion of Fire Resistance",
			180,
			8195,
			"Gives immunity to damage from fire,lava, and ranged Blaze attacks.",
			PotionCategory.POSITIVE),
	FIRE_I(
			"Potion of Fire Resistance",
			480,
			8259,
			"Gives immunity to damage from fire,lava, and ranged Blaze attacks.",
			PotionCategory.POSITIVE),
	HEALING(
			"Potion of Healing",
			0,
			8197,
			"Restores 6 health per potion's tier.",
			PotionCategory.POSITIVE),
	HEALING_II(
			"Potion of Healing II",
			0,
			8229,
			"Restores 6 health per potion's tier.",
			PotionCategory.POSITIVE),
	STRENGTH(
			"Potion of Strength",
			180,
			8201,
			"Increases melee damage by 3 per potion's tier.",
			PotionCategory.POSITIVE),
	STRENGTH_I(
			"Potion of Strength",
			480,
			8265,
			"Increases melee damage by 3 per potion's tier.",
			PotionCategory.POSITIVE),
	STRENGTH_II(
			"Potion of Strength II",
			90,
			8233,
			"Increases melee damage by 3 per potion's tier.",
			PotionCategory.POSITIVE),
	//
	// Potions with Negative Effects
	//
	POISON(
			"Potion of Poison",
			45,
			8196,
			"Poisons the player, reducing the health to 1 at most.",
			PotionCategory.NEGATIVE),
	POISON_I(
			"Potion of Poison",
			120,
			8260,
			"Poisons the player, reducing the health to 1 at most.",
			PotionCategory.NEGATIVE),
	POISON_II(
			"Potion of Poison II",
			22,
			8228,
			"Poisons the player, reducing the health to 1 at most.",
			PotionCategory.NEGATIVE),
	WEAKNESS(
			"Potion of Weakness",
			90,
			8200,
			"Reduces melee damage by 2.",
			PotionCategory.NEGATIVE),
	WEAKNESS_I(
			"Potion of Weakness",
			240,
			8264,
			"Reduces melee damage by 2.",
			PotionCategory.NEGATIVE),
	SLOWNESS(
			"Potion of Slowness",
			90,
			8202,
			"Player's movement is slowed to a crouch.",
			PotionCategory.NEGATIVE),
	SLOWNESS_I(
			"Potion of Slowness",
			240,
			8266,
			"Player's movement is slowed to a crouch.",
			PotionCategory.NEGATIVE),
	HARMING(
			"Potion of Harming",
			0,
			8204,
			"Inflicts 6 damage per potion's tier.",
			PotionCategory.NEGATIVE),
	HARMING_II(
			"Potion of Harming II",
			0,
			8236,
			"Inflicts 6 damage per potion's tier.",
			PotionCategory.NEGATIVE),
	// Splash Potions
	// Base Potions
	//
	SPLASH_MUNDANE(
			"Splash Mundane Potion",
			-1,
			16384,
			MUNDANE.getDescription(),
			PotionCategory.NEUTRAL),
	//
	// Splash Potions with Positive Effects
	SPLASH_REGENERATION(
			"Splash Potion of Regeneration",
			33,
			16385,
			REGENERATION.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_REGENERATION_I(
			"Splash Potion of Regeneration",
			90,
			16449,
			REGENERATION.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_REGENERATION_II(
			"Splash Potion of Regeneration II",
			16,
			16417,
			REGENERATION.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_SWIFTNESS(
			"Splash Potion of Swiftness",
			135,
			16386,
			SWIFTNESS.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_SWIFTNESS_I(
			"Splash Potion of Swiftness",
			360,
			16450,
			SWIFTNESS.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_SWIFTNESS_II(
			"Splash Potion of Swiftness II",
			67,
			16418,
			SWIFTNESS.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_FIRE(
			"Splash Potion of Fire Resistance",
			135,
			16387,
			FIRE.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_FIRE_I(
			"Splash Potion of Fire Resistance",
			360,
			16451,
			FIRE.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_HEALING(
			"Splash Potion of Healing",
			0,
			16389,
			HEALING.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_HEALING_II(
			"Splash Potion of Healing II",
			0,
			16421,
			HEALING.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_STRENGTH(
			"Splash Potion of Strength",
			135,
			16393,
			STRENGTH.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_STRENGTH_I(
			"Splash Potion of Strength",
			360,
			16457,
			STRENGTH.getDescription(),
			PotionCategory.POSITIVE),
	SPLASH_STRENGTH_II(
			"Splash Potion of Strength II",
			67,
			16425,
			STRENGTH.getDescription(),
			PotionCategory.POSITIVE),
	//
	// Splash Potions with Negative Effects
	//
	SPLASH_POISON(
			"Splash Potion of Poison",
			33,
			16388,
			POISON.getDescription(),
			PotionCategory.NEGATIVE),
	SPLASH_POISON_I(
			"Splash Potion of Poison",
			90,
			16452,
			POISON.getDescription(),
			PotionCategory.NEGATIVE),
	SPLASH_POISON_II(
			"Splash Potion of Poison II",
			16,
			16420,
			POISON.getDescription(),
			PotionCategory.NEGATIVE),
	SPLASH_WEAKNESS(
			"Splash Potion of Weakness",
			67,
			16392,
			WEAKNESS.getDescription(),
			PotionCategory.NEGATIVE),
	SPLASH_WEAKNESS_I(
			"Splash Potion of Weakness",
			180,
			16456,
			WEAKNESS.getDescription(),
			PotionCategory.NEGATIVE),
	SPLASH_SLOWNESS(
			"Splash Potion of Slowness",
			67,
			16394,
			SLOWNESS.getDescription(),
			PotionCategory.NEGATIVE),
	SPLASH_SLOWNESS_I(
			"Splash Potion of Slowness",
			180,
			16458,
			SLOWNESS.getDescription(),
			PotionCategory.NEGATIVE),
	SPLASH_HARMING(
			"Splash Potion of Harming",
			0,
			16396,
			HARMING.getDescription(),
			PotionCategory.NEGATIVE),
	SPLASH_HARMING_II(
			"Splash Potion of Harming II",
			0,
			16428,
			HARMING.getDescription(),
			PotionCategory.NEGATIVE);
	//
	// Unused Potions
	//
	// There are 17 unused potions which all appear to be base potions. Here is
	// a list of their names and data values:
	//
	// Clear Potion: 6,7,70,71,135,161,198,199,etc.
	// Diffuse Potion: 11,75,139,203,etc.
	// Artless Potion: 13,77,141,205,etc.
	// Thin Potion: 14,15,78,79,142,143,206,207,etc.
	// Bungling Potion: 22,23,86,87,150,151,213,214,etc.
	// Smooth Potion: 27,91,155,etc.
	// Suave Potion: 29,93,157,etc.
	// Debonair Potion: 30,31,94,95,158,159,etc.
	// Charming Potion: 38,102,103,166,etc.
	// Refined Potion: 43,134,171,etc.
	// Cordial Potion: 45,109,173,etc.
	// Sparkling Potion: 46,47,110,111,174,175,etc.
	// Potent Potion: 48,112,176,etc.
	// Rank Potion: 54,55,118,119,182,183,etc.
	// Acrid Potion: 59,123,167,187,etc.
	// Gross Potion: 61,125,189,etc.
	// Stinky Potion: 62,63,126,127,190,191,etc.
	//

	private final String name;
	private final int data;
	private final int duration;
	private final String description;
	private final PotionCategory category;

	private PotionHelper(String name, int duration, int data, String description, PotionCategory category) {
		this.name = name;
		this.duration = duration;
		this.data = data;
		this.description = description;
		this.category = category;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the data
	 */
	public int getData() {
		return data;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the category
	 */
	public PotionCategory getCategory() {
		return category;
	}

	public enum PotionCategory {
		POSITIVE,
		NEUTRAL,
		NEGATIVE,
		UNUSED;
	}
}
