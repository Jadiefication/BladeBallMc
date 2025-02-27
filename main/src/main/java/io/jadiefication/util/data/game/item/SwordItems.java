package io.jadiefication.util.data.game.item;

import io.jadiefication.util.game.start.ball.BladeBall;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public interface SwordItems {

    ItemStack diamondSword = BladeBall.item;
    ItemStack shield = ItemStack.builder(Material.SHIELD).build();
}
