package io.dogsbean.deathanimation;

import io.dogsbean.deathanimation.listeners.PlayerListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        loadConfig();
        registerListeners();
    }

    private void loadConfig() {
        getConfig().options().copyDefaults();
        this.saveDefaultConfig();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }
}
