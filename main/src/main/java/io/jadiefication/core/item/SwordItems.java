package io.jadiefication.core.item;

import io.jadiefication.core.ball.BladeBall;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public interface SwordItems {

    ItemStack diamondSword = BladeBall.item;
    ItemStack shield = ItemStack.builder(Material.SHIELD).build();
}
