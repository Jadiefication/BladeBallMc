package io.jadiefication.game.prestart.gui;

import io.jadiefication.AbilitiesHolder;
import io.jadiefication.util.gui.Border;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

public class AbilitySelectionMenu extends Inventory {

    public static boolean hasAbility = false;
    public static boolean hasSword = false;

    /*public static final ItemStack diamondScythe = CustomItem.registerItem(Component.text("§b§lDiamond Scythe"), List.of(), Material.DIAMOND_SWORD,
            1, event -> {
                PlayerUseItemEvent e = ((PlayerUseItemEvent) event);
                final Player player = e.getPlayer();
                ParticleGenerator.spawnSphereParticles(Nimoh.instanceContainer, player.getPosition(),
                        0.5, 0.5, 0.5, Particle.SWEEP_ATTACK, 1);
            });*/

    public AbilitySelectionMenu() {
        super(InventoryType.CHEST_6_ROW, Component.text("Abilities"));

        List<ItemStack> abilities = List.of(AbilitiesHolder.dash, AbilitiesHolder.superJump, AbilitiesHolder.platform);
        for (int i = 0; i < 3; i++) {
            setItemStack(21 + i, abilities.get(i));
        }
        setItemStack(31, ItemStack.builder(Material.SHIELD).build());

        Border.setInventoryBorder(this);
    }
}
