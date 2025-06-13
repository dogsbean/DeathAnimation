package io.dogsbean.deathanimation.settings;

import io.dogsbean.deathanimation.Main;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class Settings {

    private static final boolean DEFAULT_NATURAL_RESPAWN = false;
    private static final boolean DEFAULT_SET_HEALTH_ON_DEATH = true;
    private static final int DEFAULT_RESPAWN_DELAY = 20; // 1 second (20 tick)

    private static final String NATURAL_RESPAWN_KEY = "settings.natural-respawn";
    private static final String SET_HEALTH_ON_DEATH_KEY = "settings.set-health-on-death";
    private static final String RESPAWN_DELAY_KEY = "settings.respawn-delay";

    private final boolean naturalRespawn;
    private final boolean setHealthOnDeath;
    private final int respawnDelay;

    public Settings(Main plugin) {
        FileConfiguration config = plugin.getConfig();

        this.naturalRespawn = config.getBoolean(NATURAL_RESPAWN_KEY, DEFAULT_NATURAL_RESPAWN);
        this.setHealthOnDeath = config.getBoolean(SET_HEALTH_ON_DEATH_KEY, DEFAULT_SET_HEALTH_ON_DEATH);
        this.respawnDelay = Math.max(1, config.getInt(RESPAWN_DELAY_KEY, DEFAULT_RESPAWN_DELAY));

        logSettings(plugin);
    }

    private void logSettings(Main plugin) {
        plugin.getLogger().info("SETTINGS LOADED:");
        plugin.getLogger().info("  - Natural Respawn: " + naturalRespawn);
        plugin.getLogger().info("  - Set Health On Death: " + setHealthOnDeath);
        plugin.getLogger().info("  - Respawn Delay: " + respawnDelay + " ticks");
    }
}