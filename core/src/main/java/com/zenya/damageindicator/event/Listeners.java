package com.zenya.damageindicator.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class Listeners implements Listener {
    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) e.getEntity();
        Bukkit.getServer().getPluginManager().callEvent(new HologramSpawnEvent(entity, -e.getFinalDamage()));
    }

    @EventHandler
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent e) {
        if(!(e.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) e.getEntity();
        Bukkit.getServer().getPluginManager().callEvent(new HologramSpawnEvent(entity, e.getAmount()));
    }
}
