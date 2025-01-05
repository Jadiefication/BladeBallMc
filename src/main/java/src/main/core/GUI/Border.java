package src.main.core.GUI;

import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

/**
 * Utility for creating standardized GUI borders
 */
public abstract class Border {

    /**
     * Creates glass pane border around inventory
     * @param inventory Target inventory to add border to
     */
    public static void setInventoryBorder(Inventory inventory) {

        ItemStack border = ItemStack.of(Material.GRAY_STAINED_GLASS_PANE)
                .withCustomName(Component.text(" "));

        int size = inventory.getSize();
        int height = size / 9;

        for (int i = 0; i < 9; i++) {
            inventory.setItemStack(i, border);
        }

        // Bottom border
        for (int i = size - 9; i < size; i++) {
            inventory.setItemStack(i, border);
        }

        // Left and right borders
        for (int i = 1; i < height - 1; i++) {
            inventory.setItemStack(i * 9, border); // Left border
            inventory.setItemStack(i * 9 + 8, border); // Right border
        }
    }

}
