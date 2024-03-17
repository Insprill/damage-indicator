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
import com.zenya.damageindicator.util.DisplayBuilder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;

public class HologramSpawnEvent extends Event {

    private final LivingEntity ent;
    private final double amount;

    public HologramSpawnEvent(LivingEntity ent, double amount) {
        this.ent = ent;
        this.amount = amount;
        fireEvent();
    }

    public void fireEvent() {
        double offsetY = StorageFileManager.getConfig().getDouble("hologram-offset") + ent.getEyeHeight();
        double speed = StorageFileManager.getConfig().getDouble("hologram-speed");
        int duration = StorageFileManager.getConfig().getInt("hologram-duration");

        String format = StorageFileManager.getConfig().getNearestValue(amount > 0 ? "heal-format" : "damage-format", Math.abs(amount), RoundingMode.DOWN);
        String hologramText = new DisplayBuilder().withText(format).withValue(Math.abs(amount)).build();

        double offsetX = 0;
        double offsetZ = 0;
        if (StorageFileManager.getConfig().getBool("random-hologram-offset")) {
            offsetX = (Math.random() - 0.5) * 2;
            offsetZ = (Math.random() - 0.5) * 2;
        }

        DamageIndicator.PROTOCOL_NMS
                .getHologram(ent, ent.getLocation().add(offsetX, 0, offsetZ), hologramText)
                .spawn(offsetX, offsetY, offsetZ, speed, duration);
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

