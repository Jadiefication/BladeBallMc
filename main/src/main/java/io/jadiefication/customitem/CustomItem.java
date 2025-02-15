package io.jadiefication.customitem;

import net.kyori.adventure.text.Component;
import net.minestom.server.event.Event;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class CustomItem {

    private static Map<ItemStack, Consumer<Event>> items = new HashMap<>();

    public static ItemStack registerItem(Component name, List<Component> lore, Material material, int customModelData, Consumer<Event> event) {
        ItemStack item;
        if (customModelData == 0) {
            item = ItemStack.builder(material)
                    .customName(name)
                    .lore(lore)
                    .build();
        } else {
            item = ItemStack.builder(material)
                    .customModelData(customModelData)
                    .customName(name)
                    .lore(lore)
                    .build();
        }
        items.put(item, event);
        new CustomItemHolder(item, name, lore, customModelData);
        return item;
    }

    public static ItemStack registerItem(Component name, List<Component> lore, ItemStack item, int customModelData, Consumer<Event> event) {
        ItemStack newItem;
        if (customModelData == 0) {
            newItem = item.withCustomName(name)
                    .withLore(lore);
        } else {
            newItem = item.withCustomModelData(customModelData)
                    .withCustomName(name)
                    .withLore(lore);
        }
        items.put(newItem, event);
        new CustomItemHolder(item, name, lore, customModelData);
        return newItem;
    }

    public static Map<ItemStack, Consumer<Event>> getItemMap() {
        return items;
    }

    public static Consumer<Event> getItemFunctionality(ItemStack item) {
        return items.get(item);
    }

    public static List<ItemStack> getItems() {
        return items.keySet().stream().toList();
    }
}
