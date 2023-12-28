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

import com.zenya.damageindicator.file.YAMLFile;
import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.storage.ToggleManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
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

    private static final float DAMAGE_THRESHOLD = 0.01f;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.getFinalDamage() < DAMAGE_THRESHOLD)
            return;
        if (!(e.getEntity() instanceof LivingEntity))
            return;

        if (!shouldShowHologram(e.getEntity()))
            return;
        if (!StorageFileManager.getConfig().getBool("damage-indicators"))
            return;

        LivingEntity entity = (LivingEntity) e.getEntity();

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

        Bukkit.getServer().getPluginManager().callEvent(new HologramSpawnEvent(entity, -e.getFinalDamage()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent e) {
        if (e.getAmount() < DAMAGE_THRESHOLD) // Minecraft doesn't register heals of less than half a heart
            return;
        if (!(e.getEntity() instanceof LivingEntity))
            return;

        if (!shouldShowHologram(e.getEntity()))
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

    private boolean shouldShowHologram(Entity entity) {
        YAMLFile config = StorageFileManager.getConfig();
        if (entity instanceof LivingEntity && ((LivingEntity)entity).isInvisible() && config.getBool("ignore-invisible-entities"))
            return false;
        if (entity instanceof Player && ((Player) entity).isSneaking() && config.getBool("ignore-sneaking-players"))
            return false;
        if (config.listContains("disabled-worlds", entity.getWorld().getName()))
            return false;
        if (!config.isAllowed("entity-type-list", entity.getType().name()))
            return false;
        if (config.listContains("ignored-entities", entity.getName()))
            return false;
        return true;
    }

}
