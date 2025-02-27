package io.jadiefication.util.gui;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Custom player head creation utility
 */
public abstract class Heads {

    /**
     * Creates custom head from URL
     * @param url Texture URL
     * @return ItemStack of custom head
     */
    public static @NotNull ItemStack createHead(URL url) {
        // Create the base item (player head)
        ItemStack skull = ItemStack.of(Material.PLAYER_HEAD);

        // Create the NBT data for the skull texture
        String textureValue = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", url);
        String base64Value = java.util.Base64.getEncoder().encodeToString(textureValue.getBytes());

        // Create a CompoundTag for SkullOwner
        CompoundBinaryTag skullOwner = CompoundBinaryTag.builder()
                .putString("Id", "00000000-0000-0000-0000-000000000000")
                .put("Properties", CompoundBinaryTag.builder()
                        .put("textures", CompoundBinaryTag.builder()
                                .putString("Value", base64Value)
                                .build())
                        .build())
                .build();

        // Apply the NBT tag to the item
        skull = skull.withTag(Tag.NBT("SkullOwner"), skullOwner);

        return skull;
    }

    /**
     * Creates "Coming Soon" themed head
     * @return ItemStack of coming soon head
     */
    public static @NotNull ItemStack createComingSoonHead() {
        ItemStack head = createHead("46ba63344f49dd1c4f5488e926bf3d9e2b29916a6c50d610bb40a5273dc8c82");
        return head.withCustomName(Component.text("§8§lᴄᴏᴍɪɴɢ ꜱᴏᴏɴ")).withLore(List.of(Component.text("§7ᴛʜɪꜱ ɢᴀᴍᴇᴍᴏᴅᴇ ɪꜱ ᴄᴏᴍᴍɪɴɢ ꜱᴏᴏɴ,"),
                Component.text("§7ᴡᴀᴛᴄʜ ᴏᴜʀ ᴅɪꜱᴄᴏʀᴅ ꜱᴇʀᴠᴇʀ ꜰᴏʀ ᴇxᴛʀᴀ ɪɴꜰᴏʀᴍᴀᴛɪᴏɴ.")));
    }

    /**
     * Creates custom head from texture string
     * @param url Texture string
     * @return ItemStack of custom head
     */
    public static @NotNull ItemStack createHead(String url) {
        // Create the base item (player head)
        ItemStack skull = ItemStack.of(Material.PLAYER_HEAD);
        URL newUrl;
        try {
            newUrl = createUrl("http://textures.minecraft.net/texture/" + url);
        } catch (RuntimeException e) {
            System.out.println("Malformed URL");
            newUrl = createUrl("http://textures.minecraft.net/texture/46ba63344f49dd1c4f5488e926bf3d9e2b29916a6c50d610bb40a5273dc8c82");
        }

        // Create the NBT data for the skull texture
        String textureValue = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", newUrl);
        String base64Value = java.util.Base64.getEncoder().encodeToString(textureValue.getBytes());

        // Create a CompoundTag for SkullOwner
        CompoundBinaryTag skullOwner = CompoundBinaryTag.builder()
                .putString("Id", "00000000-0000-0000-0000-000000000000")
                .put("Properties", CompoundBinaryTag.builder()
                        .put("textures", CompoundBinaryTag.builder()
                                .putString("Value", base64Value)
                                .build())
                        .build())
                .build();

        // Apply the NBT tag to the item
        skull = skull.withTag(Tag.NBT("SkullOwner"), skullOwner);

        return skull;
    }

    public static URL createUrl(String url) throws RuntimeException {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
