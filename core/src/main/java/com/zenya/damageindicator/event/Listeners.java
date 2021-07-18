package com.zenya.damageindicator.event;

import com.zenya.damageindicator.scoreboard.HealthIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Listeners implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if(StorageFileManager.getConfig().listContains("disabled-worlds", e.getEntity().getWorld().getName())) return;

        //Handle damage indicator
        if(!(e.getEntity() instanceof LivingEntity)) return;
        if(StorageFileManager.getConfig().listContains("ignored-entities", e.getEntity().getName())) return;
        LivingEntity entity = (LivingEntity) e.getEntity();
        Bukkit.getServer().getPluginManager().callEvent(new HologramSpawnEvent(entity, -e.getFinalDamage()));

        //Handle health indicator
        if(e.getEntity() instanceof Player) {
            new HealthIndicator((Player) e.getEntity()).updateHealth();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent e) {
        if(StorageFileManager.getConfig().listContains("disabled-worlds", e.getEntity().getWorld().getName())) return;

        //Handle damage indicator
        if(!(e.getEntity() instanceof LivingEntity)) return;
        if(StorageFileManager.getConfig().listContains("ignored-entities", e.getEntity().getName())) return;
        if(e.getAmount() < 1) return; //Minecraft doesn't register heals of less than half a heart
        LivingEntity entity = (LivingEntity) e.getEntity();
        Bukkit.getServer().getPluginManager().callEvent(new HologramSpawnEvent(entity, e.getAmount()));

        //Handle health indicator
        if(e.getEntity() instanceof Player) {
            new HealthIndicator((Player) e.getEntity()).updateHealth();
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        if(StorageFileManager.getConfig().listContains("disabled-worlds", e.getPlayer().getWorld().getName())) return;

        new HealthIndicator(e.getPlayer()).updateHealth();
    }

    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent e) {
        if(StorageFileManager.getConfig().listContains("disabled-worlds", e.getPlayer().getWorld().getName())) {
            new HealthIndicator(e.getPlayer()).unregister();
        } else {
            new HealthIndicator(e.getPlayer()).updateHealth();
        }
    }
}
