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

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.storage.ToggleManager;
import com.zenya.damageindicator.util.DisplayBuilder;
import net.insprill.spigotutils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class HologramSpawnEvent extends Event {

    private final LivingEntity ent;
    private final double amount;
    private final boolean isCrit;

    public HologramSpawnEvent(LivingEntity ent, double amount, boolean isCrit) {
        this.ent = ent;
        this.amount = amount;
        this.isCrit = isCrit;
        fireEvent();
    }

    public void fireEvent() {
        double offsetY = StorageFileManager.getConfig().getDouble("hologram-offset") + ent.getEyeHeight();
        double speed = StorageFileManager.getConfig().getDouble("hologram-speed");
        int duration = StorageFileManager.getConfig().getInt("hologram-duration");

        String node = amount > 0
                ? "heal-format"
                : isCrit && StorageFileManager.getConfig().contains("crit-damage-format")
                ? "crit-damage-format"
                : "damage-format";
        String format = StorageFileManager.getConfig().getNearestValue(node, Math.abs(amount), RoundingMode.DOWN);
        String hologramText = new DisplayBuilder().withText(format).withValue(Math.abs(amount)).build();

        double offsetX = 0;
        double offsetZ = 0;
        if (StorageFileManager.getConfig().getBool("random-hologram-offset")) {
            offsetX = (Math.random() - 0.5) * 2;
            offsetZ = (Math.random() - 0.5) * 2;
        }

        Location location = ent.getLocation();

        DamageIndicator.PROTOCOL_NMS
                .getHologram(ent, location.add(offsetX, 0, offsetZ), filterPlayers(location), hologramText)
                .spawn(offsetX, offsetY, offsetZ, speed, duration);
    }

    @SuppressWarnings("UnstableApiUsage")
    private List<Player> filterPlayers(Location location) {
        int serverViewDistSqr = Bukkit.getViewDistance() << 5;
        List<Player> players = new ArrayList<>();
        for (Player p : ent.getWorld().getPlayers()) {
            if (p == ent) continue;
            if (MinecraftVersion.isAtLeast(MinecraftVersion.v1_18_0)) {
                // Player.canSee(Player) has been around forever,
                // but Player.canSee(Entity) was only added in 1.18 and has been "experimental" ever since :|
                if (!p.canSee(ent)) continue;
            } else if (ent instanceof Player) {
                if (!p.canSee((Player) ent)) continue;
            }
            if (p.getLocation().distanceSquared(location) > serverViewDistSqr) continue;
            if (!ToggleManager.INSTANCE.isToggled(p.getUniqueId())) continue;
            players.add(p);
        }
        return players;
    }

    //Default custom event methods
    private static final HandlerList handlers = new HandlerList();

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

