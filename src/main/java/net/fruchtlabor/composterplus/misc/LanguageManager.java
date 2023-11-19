package net.fruchtlabor.composterplus.misc;

import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.logging.Logger;

public class LanguageManager {
    private YamlConfiguration config;
    private Logger logger;

    public LanguageManager(Logger logger) {
        this.logger = logger;
    }

    public void loadLanguageFile() {
        try {
            File file = new File("plugins/ComposterPlus/lang.yml");
            logger.info("Loading language file from: " + file.getAbsolutePath());
            if (!file.exists()) {
                logger.warning("Language file not found at: " + file.getAbsolutePath());
                return;
            }
            config = YamlConfiguration.loadConfiguration(file);
            logger.info("Language file loaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMessage(String path) {
        if (config == null) {
            logger.warning("Language file not loaded!");
            return "Error: Language file not loaded";
        }
        String message = config.getString(path, null);
        if (message == null) {
            logger.warning("Message not found for path: " + path);
            return "Message not found for path: " + path;
        }
        return message;
    }
}


