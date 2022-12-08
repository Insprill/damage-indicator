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

package com.zenya.damageindicator.nms;

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.storage.ToggleManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public interface Hologram {

    Hologram spawn(double offset, double speed, long duration);

    void sendCreatePacket();

    void sendMetaPacket();

    void sendTeleportPacket(Location loc);

    void sendRemovePacket();

    void sendPacketToTracked(Object packet);

    void sendPacketToWorld(Object packet);

    default void sendPacketIfToggled(UUID uuid, Object connection, Object packet) {
        if (ToggleManager.INSTANCE.isToggled(uuid))
            sendPacket(connection, packet);
    }

    void sendPacket(Object connection, Object packet);

    class HologramRunnable extends BukkitRunnable {

        private final Hologram hologram;
        private final Entity entity;
        private final Location loc;
        private final double startY;
        private final boolean relative;
        private final double speed;
        private final double duration;

        private int tick;
        private double dy;

        public HologramRunnable(Hologram hologram, Entity entity, double offset, double speed, double duration) {
            this.hologram = hologram;
            this.entity = entity;
            this.loc = entity.getLocation();
            this.startY = loc.getY();
            this.relative = StorageFileManager.getConfig().getBool("relative-holograms");
            this.speed = speed;
            this.duration = duration;
            this.dy = offset;
        }

        @Override
        public void run() {
            if (relative) {
                entity.getLocation(loc);
            }
            loc.setY(startY + dy);
            hologram.sendTeleportPacket(loc);
            dy += speed;

            tick++;
            if (tick > duration) {
                hologram.sendRemovePacket();
                this.cancel();
            }
        }

        public void start() {
            this.runTaskTimer(DamageIndicator.INSTANCE, 0, 1);
        }

    }

}
