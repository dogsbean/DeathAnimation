package io.dogsbean.deathanimation.listeners;

import io.dogsbean.deathanimation.Main;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
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

    private void simulateDeath(Player player) {
        CraftPlayer playerCp = (CraftPlayer) player;
        EntityPlayer playerEp = playerCp.getHandle();

        playerEp.getDataWatcher().watch(6, 0.0F);
        playerEp.setFakingDeath(true);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.hidePlayer(player);
                players.showPlayer(player);
            });

            player.setHealth(player.getMaxHealth());
            playerEp.setFakingDeath(false);
            player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
            player.setWalkSpeed(0.2F);
            player.setAllowFlight(true);
        }, 20L);

        player.setWalkSpeed(0.0F);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000, -5));
        player.setVelocity(player.getLocation().getDirection().setY(1));
        player.updateInventory();
    }
}
