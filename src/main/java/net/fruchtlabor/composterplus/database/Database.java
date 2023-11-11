package net.fruchtlabor.composterplus.database;

import net.fruchtlabor.composterplus.ComposterPlus;
import net.fruchtlabor.composterplus.misc.Compost;
import net.fruchtlabor.composterplus.misc.ItemStackSerializer;
import net.fruchtlabor.composterplus.misc.Loot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

import static net.fruchtlabor.composterplus.ComposterPlus.plugin;

public class Database {

    public Connection connect() {
        try {
            if (plugin.getConfig().getBoolean("Database.MySQL")) {
                String host = plugin.getConfig().getString("Database.host");
                String port = plugin.getConfig().getString("Database.port");
                String username = plugin.getConfig().getString("Database.username");
                String password = plugin.getConfig().getString("Database.password");
                String database = plugin.getConfig().getString("Database.database");
                String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
                return DriverManager.getConnection(url, username, password);
            } else {
                File databaseFile = new File(plugin.getDataFolder(), "database.db");
                if (!databaseFile.exists()) {
                    createSQLiteFile(databaseFile);
                }
                String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();
                return DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createTables() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String createCompostTable = "CREATE TABLE IF NOT EXISTS Compost (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "item BLOB NOT NULL," +
                    "compost_level INTEGER NOT NULL" +
                    ");";
            stmt.execute(createCompostTable);

            String createLootTable = "CREATE TABLE IF NOT EXISTS Loot (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "item BLOB NOT NULL," +
                    "chance DOUBLE NOT NULL," +
                    "player_exp INTEGER NOT NULL" +
                    ");";
            stmt.execute(createLootTable);

            if (isCompostTableEmpty()){
                initializeCompostData();
                ComposterPlus.logMessage("Initial Compost-Data was filled in!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isCompostTableEmpty() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Compost")) {
            return !rs.next() || rs.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void createSQLiteFile(File databaseFile) {
        try {
            if (!databaseFile.getParentFile().exists()) {
                databaseFile.getParentFile().mkdirs();
            }
            databaseFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertCompost(Compost compost) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Compost (item, compost_level) VALUES (?, ?)")) {
            pstmt.setBytes(1, ItemStackSerializer.serialize(compost.getItem()).getBytes());
            pstmt.setInt(2, compost.getCompost_chance());
            pstmt.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void insertLoot(Loot loot) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Loot (item, chance, player_exp) VALUES (?, ?, ?)")) {
            pstmt.setBytes(1, ItemStackSerializer.serialize(loot.getItem()).getBytes());
            pstmt.setDouble(2, loot.getChance());
            pstmt.setInt(3, loot.getPlayer_exp());
            pstmt.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Compost> getComposts() {
        ArrayList<Compost> composts = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Compost")) {
            while (rs.next()) {
                ItemStack item = ItemStackSerializer.deserialize(new String(rs.getBytes("item")));
                int compost_level = rs.getInt("compost_level");
                composts.add(new Compost(item, compost_level));
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return composts;
    }

    public ArrayList<Loot> getLoots() {
        ArrayList<Loot> loots = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Loot")) {
            while (rs.next()) {
                ItemStack item = ItemStackSerializer.deserialize(new String(rs.getBytes("item")));
                double chance = rs.getDouble("chance");
                int player_exp = rs.getInt("player_exp");
                loots.add(new Loot(item, chance, player_exp));
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loots;
    }


    public void initializeCompostData() {
        insertCompostData(30, "BEETROOT_SEEDS", "DRIED_KELP", "GLOW_BERRIES", "GRASS", "GRASS_BLOCK", "HANGING_ROOTS",
                "MANGROVE_PROPAGULE", "KELP", "CHERRY_SAPLING", "MELON_SEEDS", "MOSS_CARPET", "PINK_PETALS",
                "PITCHER_POD", "PUMPKIN_SEEDS", "OAK_SAPLING", "SPRUCE_SAPLING", "BIRCH_SAPLING", "JUNGLE_SAPLING",
                "ACACIA_SAPLING", "DARK_OAK_SAPLING", "AZALEA", "FLOWERING_AZALEA",
                "SEAGRASS", "SMALL_DRIPLEAF", "SWEET_BERRIES", "TORCHFLOWER_SEEDS",
                "WHEAT_SEEDS", "OAK_LEAVES", "SPRUCE_LEAVES", "BIRCH_LEAVES", "JUNGLE_LEAVES", "ACACIA_LEAVES", "DARK_OAK_LEAVES", "MANGROVE_LEAVES", "CHERRY_LEAVES", "AZALEA_LEAVES");

        insertCompostData(50, "CACTUS", "DRIED_KELP_BLOCK", "FLOWERING_AZALEA_LEAVES", "GLOW_LICHEN", "MELON_SLICE",
                "NETHER_SPROUTS", "SUGAR_CANE", "TALL_GRASS", "TWISTING_VINES", "VINE", "WEEPING_VINES");

        insertCompostData(65, "APPLE", "AZALEA", "BEETROOT", "BIG_DRIPLEAF", "CARROT", "COCOA_BEANS", "FERN",
                "CRIMSON_FUNGUS", "WARPED_FUNGUS", "LILY_PAD", "MELON", "MOSS_BLOCK", "MUSHROOM_STEM", "NETHER_WART",
                "POTATO", "PUMPKIN", "CRIMSON_ROOTS", "WARPED_ROOTS", "SEA_PICKLE", "SHROOMLIGHT", "SPORE_BLOSSOM", "WHEAT",
                "dandelion", "poppy", "blue_orchid", "allium", "azure_bluet", "red_tulip", "orange_tulip",
                "white_tulip", "pink_tulip", "oxeye_daisy", "brown_mushroom", "red_mushroom", "sunflower",
                "lilac", "rose_bush", "peony");

        insertCompostData(85, "BAKED_POTATO", "BREAD", "COOKIE", "FLOWERING_AZALEA", "HAY_BLOCK", "BROWN_MUSHROOM_BLOCK", "RED_MUSHROOM_BLOCK",
                "NETHER_WART_BLOCK", "PITCHER_PLANT", "TORCHFLOWER", "WARPED_WART_BLOCK");

        insertCompostData(100, "CAKE", "PUMPKIN_PIE");
    }

    private void insertCompostData(int compostChance, String... materials) {
        for (String materialName : materials) {
            materialName = materialName.toUpperCase(Locale.ROOT);
            Material material = null;
            try {
                material = Material.valueOf(materialName);
                ItemStack itemStack = new ItemStack(material);
                Compost compost = new Compost(itemStack, compostChance);
                insertCompost(compost);
            }catch (Exception e){
                ComposterPlus.logMessage("Item may was removed in later version: "+materialName);
            }
        }
    }

    public void deleteCompost(ItemStack itemStack) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Compost WHERE item = ?")) {
            pstmt.setBytes(1, ItemStackSerializer.serialize(itemStack).getBytes());
            pstmt.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteLoot(ItemStack itemStack) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Loot WHERE item = ?")) {
            pstmt.setBytes(1, ItemStackSerializer.serialize(itemStack).getBytes());
            pstmt.executeUpdate();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

}
