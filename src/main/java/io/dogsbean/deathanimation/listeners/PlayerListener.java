package io.dogsbean.deathanimation.listeners;

import io.dogsbean.deathanimation.Main;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {

    private static final String RESPAWNING_METADATA_KEY = "respawning";
    private static final float ALIVE_HEALTH_VALUE = 20.0F;
    private static final float DISABLED_WALK_SPEED = 0.0F;
    private static final float NORMAL_WALK_SPEED = 0.2F;
    private static final int JUMP_AMPLIFIER = 100000;

    private final Map<UUID, BukkitTask> respawnTasks = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!isValidPlayer(player)) {
            return;
        }

        event.getDrops().clear();

        if (Main.getInstance().getSettings().isSetHealthOnDeath()) {
            player.setHealth(ALIVE_HEALTH_VALUE);
        }

        handleFakeDeath(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!isValidDamageEvent(event)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();

        if (isPlayerRespawning(victim) || isPlayerRespawning(attacker)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cleanupPlayer(event.getPlayer());
    }

    private void handleFakeDeath(Player player) {
        try {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            EntityPlayer entityPlayer = craftPlayer.getHandle();
            UUID playerId = player.getUniqueId();

            cleanupPlayer(player);

            applyDeathEffects(player);
            setPlayerRespawning(player, true);

            entityPlayer.getDataWatcher().watch(6, 0F);
            entityPlayer.setFakingDeath(true);

            BukkitTask respawnTask = new BukkitRunnable() {
                @Override
                public void run() {
                    handleRespawn(player, entityPlayer);
                    respawnTasks.remove(playerId);
                }
            }.runTaskLater(Main.getInstance(), Main.getInstance().getSettings().getRespawnDelay());

            respawnTasks.put(playerId, respawnTask);

        } catch (Exception e) {
            Main.getInstance().getLogger().severe("Erorr: " + e.getMessage());
            resetPlayerState(player);
        }
    }

    private void handleRespawn(Player player, EntityPlayer entityPlayer) {
        try {
            if (Main.getInstance().getSettings().isNaturalRespawn()) {
                teleportToSpawn(player);
            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.isOnline()) {
                    entityPlayer.getDataWatcher().watch(6, ALIVE_HEALTH_VALUE);
                    entityPlayer.setFakingDeath(false);

                    onlinePlayer.hidePlayer(player);
                    onlinePlayer.showPlayer(player);
                }
            }

            resetPlayerState(player);

        } catch (Exception e) {
            Main.getInstance().getLogger().severe("Error: " + e.getMessage());
            resetPlayerState(player);
        }
    }

    private void applyDeathEffects(Player player) {
        PotionEffect jumpEffect = new PotionEffect(
                PotionEffectType.JUMP,
                Main.getInstance().getSettings().getRespawnDelay() + 20,
                JUMP_AMPLIFIER,
                true,
                false
        );
        player.addPotionEffect(jumpEffect);

        player.setWalkSpeed(DISABLED_WALK_SPEED);
    }

    private void resetPlayerState(Player player) {
        if (player.isOnline()) {
            setPlayerRespawning(player, false);
            player.setWalkSpeed(NORMAL_WALK_SPEED);
            player.removePotionEffect(PotionEffectType.JUMP);
        }
    }

    private void teleportToSpawn(Player player) {
        try {
            Location spawnLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
            if (spawnLocation != null) {
                player.teleport(spawnLocation);
            }
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("Failed to teleport: " + e.getMessage());
        }
    }

    private void cleanupPlayer(Player player) {
        BukkitTask task = respawnTasks.remove(player.getUniqueId());
        if (task != null) task.cancel();

        resetPlayerState(player);
    }

    private boolean isValidPlayer(Player player) {
        return player != null && player.getType() == EntityType.PLAYER;
    }

    private boolean isValidDamageEvent(EntityDamageByEntityEvent event) {
        return event.getEntity() instanceof Player &&
                event.getDamager() instanceof Player;
    }

    private boolean isPlayerRespawning(Player player) {
        return player.hasMetadata(RESPAWNING_METADATA_KEY);
    }

    private void setPlayerRespawning(Player player, boolean respawning) {
        if (respawning) {
            player.setMetadata(RESPAWNING_METADATA_KEY, new FixedMetadataValue(Main.getInstance(), true));
        } else {
            player.removeMetadata(RESPAWNING_METADATA_KEY, Main.getInstance());
        }
    }

    public void cleanup() {
        respawnTasks.values().forEach(task -> {
            if (task != null) task.cancel();
        });
        respawnTasks.clear();
        Bukkit.getOnlinePlayers().forEach(this::resetPlayerState);
    }
}