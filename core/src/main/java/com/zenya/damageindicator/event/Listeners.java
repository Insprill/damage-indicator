package com.zenya.damageindicator.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class Listeners implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if(e.isCancelled()) return;
        if(!(e.getEntity() instanceof Creature || e.getEntity() instanceof Player)) return;
        LivingEntity entity = (LivingEntity) e.getEntity();
        Bukkit.getServer().getPluginManager().callEvent(new HologramSpawnEvent(entity, -e.getFinalDamage()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent e) {
        if(e.isCancelled()) return;
        if(!(e.getEntity() instanceof Creature || e.getEntity() instanceof Player)) return;
        LivingEntity entity = (LivingEntity) e.getEntity();
        Bukkit.getServer().getPluginManager().callEvent(new HologramSpawnEvent(entity, e.getAmount()));
    }
}
