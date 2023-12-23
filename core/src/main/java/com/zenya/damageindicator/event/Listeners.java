/*
 *     Damage Indicator
 *     Copyright (C) 2021  Zenya
 *     Copyright (C) 2021-2022  Pierce Thompson
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.zenya.damageindicator.event;

import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.storage.ToggleManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.getFinalDamage() < 0.01)
            return;
        if (!(e.getEntity() instanceof LivingEntity))
            return;
        LivingEntity entity = (LivingEntity) e.getEntity();
        if (StorageFileManager.getConfig().listContains("disabled-worlds", entity.getWorld().getName()))
            return;
        if (!StorageFileManager.getConfig().getBool("damage-indicators"))
            return;

        if (!(entity instanceof Player) && StorageFileManager.getConfig().getBool("only-show-entity-damage-from-players")) {
            if (!(e instanceof EntityDamageByEntityEvent))
                return;
            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
            if (ev.getDamager() instanceof Projectile) {
                if (!(((Projectile) ev.getDamager()).getShooter() instanceof Player))
                    return;
            } else if (!(((EntityDamageByEntityEvent) e).getDamager() instanceof Player)) {
                return;
            }
        }

        if (entity.isInvisible() && StorageFileManager.getConfig().getBool("ignore-invisible-entities"))
            return;
        if (!StorageFileManager.getConfig().isAllowed("entity-type-list", entity.getType().name()))
            return;
        if (StorageFileManager.getConfig().listContains("ignored-entities", entity.getName()))
            return;

        Bukkit.getServer().getPluginManager().callEvent(new HologramSpawnEvent(entity, -e.getFinalDamage()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof LivingEntity))
            return;
        if (e.getAmount() < 1) // Minecraft doesn't register heals of less than half a heart
            return;
        if (StorageFileManager.getConfig().listContains("disabled-worlds", e.getEntity().getWorld().getName()))
            return;
        if (!StorageFileManager.getConfig().getBool("heal-indicators"))
            return;
        if (!StorageFileManager.getConfig().isAllowed("entity-type-list", e.getEntity().getType().name()))
            return;
        if (StorageFileManager.getConfig().listContains("ignored-entities", e.getEntity().getName()))
            return;

        Bukkit.getServer().getPluginManager().callEvent(new HologramSpawnEvent((LivingEntity) e.getEntity(), e.getAmount()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        ToggleManager.INSTANCE.isToggled(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinEvent(PlayerQuitEvent e) {
        ToggleManager.INSTANCE.uncacheToggle(e.getPlayer().getUniqueId());
    }

}
