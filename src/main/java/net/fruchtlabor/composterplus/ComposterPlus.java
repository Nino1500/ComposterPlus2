package net.fruchtlabor.composterplus;

import net.fruchtlabor.composterplus.commands.AdminCommands;
import net.fruchtlabor.composterplus.database.Database;
import net.fruchtlabor.composterplus.events.ComposterInteraction;
import net.fruchtlabor.composterplus.events.PlaceRemoveInteractComposter;
import net.fruchtlabor.composterplus.misc.Compost;
import net.fruchtlabor.composterplus.misc.LanguageManager;
import net.fruchtlabor.composterplus.misc.Loot;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class ComposterPlus extends JavaPlugin {

    public static Plugin plugin;
    public static Database database;
    public static List<Compost> composts = Collections.synchronizedList(new ArrayList<Compost>());
    public static List<Loot> loots = Collections.synchronizedList(new ArrayList<Loot>());
    public static LanguageManager languageManager;

    @Override
    public void onEnable() {

        plugin = this;
        database = new Database();
        database.createTables();
        loots = database.getLoots();
        composts = database.getComposts();
        this.getServer().getPluginManager().registerEvents(new PlaceRemoveInteractComposter(), this);
        this.getServer().getPluginManager().registerEvents(new ComposterInteraction(), this);
        this.getCommand("cp").setExecutor(new AdminCommands());
        generateDefaultFiles();
        languageManager = new LanguageManager(this.getLogger());
        languageManager.loadLanguageFile();

    }

    public static void logMessage(String message) {
        Logger logger = plugin.getLogger();
        logger.info(message);
    }

    private void generateDefaultFiles() {
        saveResource("config.yml", false);
        saveResource("lang.yml", false);
    }

}
