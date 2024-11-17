package io.dogsbean.deathanimation.listeners;

import io.dogsbean.deathanimation.Main;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (e.getEntity() == null || e.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        e.getDrops().clear();
        fakingDeath(e.getEntity().getPlayer());
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (e.getEntity() == null || e.getDamager() == null) {
            return;
        }

        Player victim = (Player) e.getEntity();
        Player attacker = (Player) e.getDamager();

        if (victim.hasMetadata("respawning") || attacker.hasMetadata("respawning")) {
            e.setCancelled(true);
        }
    }

    private void fakingDeath(Player player) {
        CraftPlayer playerCp = (CraftPlayer)player;
        EntityPlayer playerEp = playerCp.getHandle();

        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10 * 20, 100000, true, false));
        player.setWalkSpeed(0.0F);
        player.setMetadata("respawning", new FixedMetadataValue(Main.getInstance(), true));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Main.getInstance().getConfig().getBoolean("NATURAL-RESPAWN")) {
                    Location spawnLoc = Bukkit.getWorlds().get(0).getSpawnLocation();
                    if (spawnLoc != null) player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }

                Bukkit.getOnlinePlayers().forEach(players -> {
                    playerEp.getDataWatcher().watch(6, 20.0F);
                    playerEp.setFakingDeath(false);
                    players.hidePlayer(player);
                    players.showPlayer(player);
                    player.removeMetadata("respawning", Main.getInstance());
                });
                player.setWalkSpeed(0.2F);
                player.removePotionEffect(PotionEffectType.JUMP);
            }
        }.runTaskLater(Main.getInstance(), 20L);

        player.setHealth(20.0F);
        playerEp.getDataWatcher().watch(6, 0.0F);
        playerEp.setFakingDeath(true);
    }
}
