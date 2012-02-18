/**
 * 
 */
package org.selurgniman.bukkit.theneedfuls.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * 
 */
public class NetherBrickRecipe extends ShapedRecipe {
	public NetherBrickRecipe() {
		super(new ItemStack(Material.NETHER_BRICK, 4));
		this.shape("nn", "nn");
		this.setIngredient('n', Material.NETHERRACK);
	}
}
