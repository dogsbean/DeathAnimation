package io.dogsbean.deathanimation.listeners;

import io.dogsbean.deathanimation.Main;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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
        simulateDeath(e.getEntity().getPlayer());
    }

    public static void simulateDeath(Player player) {
        CraftPlayer playerCp = (CraftPlayer)player;
        EntityPlayer playerEp = playerCp.getHandle();

        Location location = player.getLocation();
        location.add(0, 0.1, 0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10 * 20, 100000, true, false));
        player.setWalkSpeed(0.0F);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setWalkSpeed(0.2F);
                player.removePotionEffect(PotionEffectType.JUMP);
            }
        }.runTaskLater(Main.getInstance(), 20L);

        player.setHealth(20.0F);
        player.teleport(location);
        playerEp.getDataWatcher().watch(6, 0.0F);
        playerEp.setFakingDeath(true);
    }
}
