package io.jadiefication.game.prestart.gui;

import io.jadiefication.CustomItemsHolder;
import io.jadiefication.Nimoh;
import io.jadiefication.core.ball.BladeBall;
import io.jadiefication.core.gui.Border;
import io.jadiefication.customitem.CustomItem;
import io.jadiefication.particlegenerator.ParticleGenerator;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.particle.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AbilitySelectionMenu extends Inventory {

    public AbilitySelectionMenu() {
        super(InventoryType.CHEST_6_ROW, Component.text("Abilities"));

        List<ItemStack> abilities = List.of(CustomItemsHolder.dash, CustomItemsHolder.superJump, CustomItemsHolder.platform);
        List<ItemStack> swords = List.of(BladeBall.item, CustomItem.registerItem(Component.text("§b§lDiamond Scythe"), List.of(), Material.DIAMOND_SWORD,
                1, event -> {
                    PlayerUseItemEvent e = ((PlayerUseItemEvent) event);
                    final Player player = e.getPlayer();
                    ParticleGenerator.spawnSphereParticles(Nimoh.instanceContainer, player.getPosition(),
                            0.5, 0.5, 0.5, Particle.SWEEP_ATTACK, 1);
                }));
        for (int i = 0; i < 3; i++) {
            setItemStack(21 + i, abilities.get(i));
        }
        setItemStack(31, swords.getFirst());
        setItemStack(40, swords.getLast());

        Border.setInventoryBorder(this);
    }
}
