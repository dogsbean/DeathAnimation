package io.dogsbean.deathanimation;

import io.dogsbean.deathanimation.listeners.PlayerListener;
import io.dogsbean.deathanimation.settings.Settings;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Main extends JavaPlugin {

    private static Main instance;
    private Settings settings;
    private PlayerListener playerListener;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        if (!initializePlugin()) {
            getLogger().severe("Failed to initialize the plugin");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("DeathAnimation Enabled");
    }

    @Override
    public void onDisable() {
        if (playerListener != null) {
            playerListener.cleanup();
        }
        instance = null;
        getLogger().info("DeathAnimation Disabled");
    }

    private boolean initializePlugin() {
        try {
            loadConfig();
            settings = new Settings(this);
            playerListener = new PlayerListener();
            registerListeners();
            return true;
        } catch (Exception e) {
            getLogger().severe("Error: " + e.getMessage());
            return false;
        }
    }

    private void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(playerListener, this);
    }
}