package io.jadiefication.util.data.player;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.jadiefication.Nimoh;
import io.jadiefication.permission.sql.PermissionSQLHandler;
import io.jadiefication.util.Handler;
import io.jadiefication.customitem.CustomItemHolder;
import io.jadiefication.permission.PermissionablePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Fuck ORMs
 *
 * @Author Jade
 */
public interface PlayerDataHandler extends Handler {

    static void start() throws SQLException {
        Config.config.setJdbcUrl(Nimoh.url);
        Config.config.setMaximumPoolSize(10);
        Config.config.setMinimumIdle(5);
        Config.config.setIdleTimeout(300000);
        Config.config.setMaxLifetime(600000);
        Config.config.addDataSourceProperty("journal_mode", "WAL");
        Config.config.addDataSourceProperty("synchronous", "NORMAL");
        Config.config.setMaximumPoolSize(1); // SQLite works better with single connection
        Config.config.setConnectionTimeout(30000);
        Config.dataSource = new HikariDataSource(Config.config);
        Nimoh.connection = Config.dataSource.getConnection();
        startDatabase();
    }

    private static void getCurrency(Player player) {
        Nimoh.executorService.submit(() -> {
            try {
                PreparedStatement statement = Nimoh.connection.prepareStatement("SELECT currency FROM player_currency WHERE player_uuid = ?");
                statement.setString(1, player.getUuid().toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    ((PermissionablePlayer) player).currencyAmount = resultSet.getInt("currency");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private static void getWins(Player player) {
        Nimoh.executorService.submit(() -> {
            try {
                PreparedStatement statement = Nimoh.connection.prepareStatement("SELECT wins FROM player_currency WHERE player_uuid = ?");
                statement.setString(1, player.getUuid().toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    ((PermissionablePlayer) player).winAmount = resultSet.getInt("wins");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    static void getData(Player player) {
        Nimoh.executorService.submit(() -> {
            getWins(player);
            getCurrency(player);
            try {
                PreparedStatement statement = Nimoh.connection.prepareStatement("SELECT * FROM player_inventory WHERE player_uuid = ?");
                statement.setString(1, player.getUuid().toString());

                try (ResultSet resultSet = statement.executeQuery()) {
                    // Iterate over the result set and process the rows
                    while (resultSet.next()) {
                        int slot = resultSet.getInt("slot");
                        String itemId = resultSet.getString("itemid");
                        int customModelData = resultSet.getInt("custommodeldata");
                        String customName = resultSet.getString("customname");
                        String customLore = resultSet.getString("customlore");

                        List<Component> lore = new ArrayList<>();


                        customLore.lines().forEach(line -> {
                            lore.add(Component.text(line));
                        });

                        try {
                            ItemStack item;
                            if (customName != null && !customLore.isEmpty()) {
                                item = ItemStack.builder(Objects.requireNonNull(Material.fromNamespaceId(itemId)))
                                        .customModelData(customModelData)
                                        .lore(lore)
                                        .customName(Component.text(customName))
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
                }
            } catch (SQLException e) {
                System.out.println("Could not connect to DB");
                e.printStackTrace();
            }
        });
    }

    static void updateData(Player player) {
        Nimoh.executorService.submit(() ->{
            setWins(((PermissionablePlayer) player));
            setCurrency(((PermissionablePlayer) player));
            PermissionSQLHandler.setPermissions((PermissionablePlayer) player);
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
        try {
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
            String createPlayerStatsTable = """
                    CREATE TABLE IF NOT EXISTS player_currency (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      player_uuid TEXT NOT NULL,
                      currency INTEGER,
                      wins INTEGER
                    )
                    """;

            try (Statement statement = Nimoh.connection.createStatement()) {
                statement.execute(createPlayerTable);
                statement.execute(createPlayerStatsTable);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void setPlayerData(String uuid, int slot, ItemStack item) {
        try {
            // Check if the row already exists
            String checkQuery = "SELECT COUNT(*) FROM player_inventory WHERE player_uuid = ? AND slot = ?";
            try (PreparedStatement checkStatement = Nimoh.connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, uuid);
                checkStatement.setInt(2, slot);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    resultSet.next();
                    if (resultSet.getInt(1) > 0) {
                        // Row exists, update it
                        String updateQuery = "UPDATE player_inventory SET itemid = ?, custommodeldata = ?, customname = ?, customlore = ? WHERE player_uuid = ? AND slot = ?";
                        try (PreparedStatement updateStatement = Nimoh.connection.prepareStatement(updateQuery)) {
                            prepareStatements(uuid, slot, item, updateStatement);
                        }
                    } else {
                        // Row does not exist, insert it
                        String insertQuery = "INSERT INTO player_inventory (player_uuid, slot, itemid, custommodeldata, customname, customlore) VALUES (?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement insertStatement = Nimoh.connection.prepareStatement(insertQuery)) {
                            prepareStatements(uuid, slot, item, insertStatement);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void setCurrency(PermissionablePlayer player) {
        Nimoh.executorService.submit(() -> {
            try {
                // First check if player exists
                String checkQuery = "SELECT COUNT(*) FROM player_currency WHERE player_uuid = ?";
                try (PreparedStatement checkStatement = Nimoh.connection.prepareStatement(checkQuery)) {
                    checkStatement.setString(1, player.getUuid().toString());
                    ResultSet rs = checkStatement.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        // Update existing player
                        PreparedStatement updateStatement = Nimoh.connection.prepareStatement(
                                "UPDATE player_currency SET currency = ? WHERE player_uuid = ?"
                        );
                        updateStatement.setInt(1, player.currencyAmount);
                        updateStatement.setString(2, player.getUuid().toString());
                        updateStatement.executeUpdate();
                    } else {
                        // Insert new player
                        PreparedStatement insertStatement = Nimoh.connection.prepareStatement(
                                "INSERT INTO player_currency (player_uuid, currency) VALUES (?, ?)"
                        );
                        insertStatement.setString(1, player.getUuid().toString());
                        insertStatement.setInt(2, player.currencyAmount);
                        insertStatement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private static void setWins(PermissionablePlayer player) {
        Nimoh.executorService.submit(() -> {
            try {
                // First check if player exists
                String checkQuery = "SELECT COUNT(*) FROM player_currency WHERE player_uuid = ?";
                try (PreparedStatement checkStatement = Nimoh.connection.prepareStatement(checkQuery)) {
                    checkStatement.setString(1, player.getUuid().toString());
                    ResultSet rs = checkStatement.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        // Update existing player
                        PreparedStatement updateStatement = Nimoh.connection.prepareStatement(
                                "UPDATE player_currency SET wins = ? WHERE player_uuid = ?"
                        );
                        updateStatement.setInt(1, player.winAmount);
                        updateStatement.setString(2, player.getUuid().toString());
                        updateStatement.executeUpdate();
                    } else {
                        // Insert new player
                        PreparedStatement insertStatement = Nimoh.connection.prepareStatement(
                                "INSERT INTO player_currency (player_uuid, wins) VALUES (?, ?)"
                        );
                        insertStatement.setString(1, player.getUuid().toString());
                        insertStatement.setInt(2, player.winAmount);
                        insertStatement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private static void prepareStatements(String uuid, int slot, ItemStack item, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, uuid);
        preparedStatement.setInt(2, slot);
        preparedStatement.setString(3, item.material().name());
        Optional<CustomItemHolder> customItemOptional = CustomItemHolder.hasItem(item);

        String customName = item.material().name();
        List<Component> lore;
        AtomicReference<String> loreLine;
        Integer customModelData = null;

        if (customItemOptional.isPresent()) {
            CustomItemHolder customItem = customItemOptional.get();
            customName = ((TextComponent) customItem.title()).content();
            lore = customItem.lore();
            loreLine = new AtomicReference<>("");
            lore.forEach(line -> {
                loreLine.getAndSet(loreLine.get() + ((TextComponent) line).content() + "\n");
            });
            customModelData = customItem.customModelData();
        } else {
            loreLine = new AtomicReference<>("");
        }

        preparedStatement.setInt(4, customModelData);
        preparedStatement.setString(5, customName);
        preparedStatement.setString(6, loreLine.get());
        preparedStatement.executeUpdate();
    }

    class Config {
        public static HikariConfig config = new HikariConfig();
        public static HikariDataSource dataSource;
    }
}
