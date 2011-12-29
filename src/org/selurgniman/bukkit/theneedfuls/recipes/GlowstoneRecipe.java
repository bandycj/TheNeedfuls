/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Dec 29,
 *         2011
 */
public class GlowstoneRecipe extends ShapedRecipe {

	public GlowstoneRecipe() {
		super(new ItemStack(Material.GLOWSTONE,1));
		this.shape("g", "t", "g");
		this.setIngredient('g', Material.GLASS);
		this.setIngredient('t', Material.TORCH);
	}
}
