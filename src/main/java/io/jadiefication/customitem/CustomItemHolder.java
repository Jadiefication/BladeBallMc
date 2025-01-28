package io.jadiefication.customitem;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public record CustomItemHolder(ItemStack item, Component title, List<Component> lore, int customModelData) {

    public CustomItemHolder(ItemStack item, Component title, List<Component> lore, int customModelData) {
        this.item = item;
        this.title = title;
        this.lore = lore;
        this.customModelData = customModelData;

        CustomItemHolder.addItem(this);
    }

    private static final List<CustomItemHolder> items = new ArrayList<>();

    public static List<CustomItemHolder> getItems() {
        return items;
    }

    public static Optional<CustomItemHolder> hasItem(ItemStack item) {
        Optional<CustomItemHolder> customItem = getItems().stream()
                .filter(itemInList -> itemInList.item.equals(item))
                .findFirst(); // Returns an Optional

        return customItem;

    }

    private static void addItem(CustomItemHolder item) {
        CustomItemHolder.items.add(item);
    }
}
