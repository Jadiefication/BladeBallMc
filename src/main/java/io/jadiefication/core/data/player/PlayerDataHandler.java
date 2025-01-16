package io.jadiefication.core.data.player;

import io.jadiefication.core.Handler;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static io.jadiefication.Nimoh.executorService;

/**
 * Fuck ORMs
 *
 * @Author Jade
 */
public interface PlayerDataHandler extends Handler {

    String url = "jdbc:sqlite:data.db";

    static void start() {
        File file = new File("data.db");
        if (!file.exists()) {
            try {
                boolean ignored = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        startDatabase();
    }

    static void getData(Player player) {
        executorService.submit(() -> {
            try (Connection connection = DriverManager.getConnection(url)) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM player_inventory WHERE player_uuid = ?");
                statement.setString(1, player.getUuid().toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    // Iterate over the result set and process the rows
                    while (resultSet.next()) {
                        int slot = resultSet.getInt("slot");
                        String itemId = resultSet.getString("itemid");
                        int customModelData = resultSet.getInt("custommodeldata");
                        String customName = resultSet.getString("customname");
                        String customLore = resultSet.getString("customlore");

                        List<net.kyori.adventure.text.Component> lore = new ArrayList<>();


                        customLore.lines().forEach(line -> {
                            lore.add(net.kyori.adventure.text.Component.text(line));
                        });

                        try {
                            ItemStack item;
                            if (customName != null && !customLore.isEmpty()) {
                                item = ItemStack.builder(Objects.requireNonNull(Material.fromNamespaceId(itemId)))
                                        .customModelData(customModelData)
                                        .lore(lore)
                                        .customName(net.kyori.adventure.text.Component.text(customName))
                                        .build();
                            } else {
                                item = ItemStack.builder(Objects.requireNonNull(Material.fromNamespaceId(itemId)))
                                        .customModelData(customModelData)
                                        .build();
                            }
                            player.getInventory().setItemStack(slot, item);
                        } catch (NullPointerException e) {
                            System.out.println("Could not create item from material, check your code dumbass");
                            e.printStackTrace();
                        }
                    }
                } finally {
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Could not connect to DB");
                e.printStackTrace();
            }
        });
    }

    static void updateData(Player player) {
        executorService.submit(() ->{
            String uuid = player.getUuid().toString();
            AtomicInteger slot = new AtomicInteger();
            Arrays.stream(player.getInventory().getItemStacks()).forEach((item) -> {
                if (item != ItemStack.AIR) {
                    setPlayerData(uuid, slot.get(), item);
                }
                slot.getAndIncrement();
            });
        });
    }

    private static void startDatabase() {
        try (Connection connection = DriverManager.getConnection(url)) {
            // Create the tables if they do not exist
            String createPlayerTable = """
                CREATE TABLE IF NOT EXISTS player_inventory (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_uuid TEXT NOT NULL,
                    slot INTEGER CHECK(slot >= 0),
                    itemid TEXT NOT NULL,
                    custommodeldata INTEGER,
                    customname TEXT,
                    customlore TEXT
                )
            """;

            try (Statement statement = connection.createStatement()) {
                statement.execute(createPlayerTable);
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void setPlayerData(String uuid, int slot, ItemStack item) {
        try (Connection connection = DriverManager.getConnection(url)) {
            // Check if the row already exists
            String checkQuery = "SELECT COUNT(*) FROM player_inventory WHERE player_uuid = ? AND slot = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, uuid);
                checkStatement.setInt(2, slot);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    resultSet.next();
                    if (resultSet.getInt(1) > 0) {
                        // Row exists, update it
                        String updateQuery = "UPDATE player_inventory SET itemid = ?, custommodeldata = ?, customname = ?, customlore = ? WHERE player_uuid = ? AND slot = ?";
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            prepareStatements(uuid, slot, item, updateStatement);
                        }
                    } else {
                        // Row does not exist, insert it
                        String insertQuery = "INSERT INTO player_inventory (player_uuid, slot, itemid, custommodeldata, customname, customlore) VALUES (?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                            prepareStatements(uuid, slot, item, insertStatement);
                        }
                    }
                }
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void prepareStatements(String uuid, int slot, ItemStack item, PreparedStatement preparedStatement) throws SQLException {
        int cmd = 0;
        String customName = null;
        AtomicReference<String> customLore = null;

        preparedStatement.setString(1, uuid);
        preparedStatement.setInt(2, slot);
        preparedStatement.setString(3, item.material().name());

        try {
            cmd = Objects.requireNonNull(item.get(ItemComponent.CUSTOM_MODEL_DATA));
            customName = ((TextComponent) Objects.requireNonNull(item.get(ItemComponent.CUSTOM_NAME))).getText();
            AtomicReference<String> finalCustomLore = customLore;
            Objects.requireNonNull(item.get(ItemComponent.LORE)).forEach(line -> {
                String text = ((TextComponent) line).getText();
                String s;
                if (finalCustomLore == null) s = text;
                else s = finalCustomLore.get() + "\n" + text;
                finalCustomLore.set(s);
            });
            customLore = finalCustomLore;

        } catch (Exception ignored) {
            cmd = 0;
            customName = null;
            customLore = null;
        }

        preparedStatement.setInt(4, cmd);
        preparedStatement.setString(5, customName);
        preparedStatement.setString(6, String.valueOf(customLore));
        preparedStatement.executeUpdate();
    }

}
