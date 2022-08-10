package com.zenya.damageindicator.nms.fallback;

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(LivingEntity ent, String text) {
        return new HologramImpl(ent, text);
    }

    public static class HologramImpl implements Hologram {

        private ArmorStand armorStand;
        private final LivingEntity entity;
        private final String text;
        private double dy;

        public HologramImpl(LivingEntity entity, String text) {
            this.entity = entity;
            this.dy = 0;
            this.text = text;
        }

        @Override
        public Hologram spawn(double offset, double speed, long duration) {
            sendCreatePacket();
            sendMetaPacket();
            this.dy += offset;

            new BukkitRunnable() {
                int tick = 0;
                final boolean relative = StorageFileManager.getConfig().getBool("relative-holograms");
                final Location loc = entity.getLocation();
                final double startY = loc.getY();

                @Override
                public void run() {
                    if (relative) {
                        entity.getLocation(loc);
                    }
                    loc.setY(startY + dy);
                    sendTeleportPacket(loc);
                    dy += speed;

                    tick++;
                    if (tick > duration) {
                        sendRemovePacket();
                        this.cancel();
                    }
                }
            }.runTaskTimer(DamageIndicator.INSTANCE, 0, 1);
            return this;
        }

        @Override
        public void sendCreatePacket() {
            armorStand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
        }

        @Override
        public void sendMetaPacket() {
            armorStand.setInvulnerable(true);
            armorStand.setVisible(false);
            armorStand.setMarker(true);
            armorStand.setSmall(true);
            armorStand.setGravity(false);
            armorStand.setCustomName(text);
            armorStand.setCustomNameVisible(true);
        }

        @Override
        public void sendTeleportPacket(Location loc) {
            armorStand.teleport(loc);
        }

        @Override
        public void sendRemovePacket() {
            armorStand.remove();
        }

        @Override
        public void sendPacketToTracked(Object packet) {
            throw new UnsupportedOperationException("Fallback isn't packet based!");
        }

        @Override
        public void sendPacketToAllInWorld(Object packet) {
            throw new UnsupportedOperationException("Fallback isn't packet based!");
        }

    }

}
