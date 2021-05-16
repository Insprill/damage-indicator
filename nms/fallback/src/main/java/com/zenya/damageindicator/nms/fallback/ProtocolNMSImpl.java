package com.zenya.damageindicator.nms.fallback;

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ProtocolNMSImpl implements ProtocolNMS {
    @Override
    public Hologram getHologram(Player player, LivingEntity ent, String text) {
        return new HologramImpl(player, ent, text);
    }

    public class HologramImpl implements Hologram {
        Player player;
        ArmorStand armorStand;
        LivingEntity ent;
        Location initialLoc;
        String text;
        double dy;

        public HologramImpl(Player player, LivingEntity ent, String text) {
            this.player = player;
            this.ent = ent;
            this.initialLoc = ent.getEyeLocation();
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

                @Override
                public void run() {
                    sendTeleportPacket();
                    dy += speed;

                    tick++;
                    if(tick > duration) {
                        sendRemovePacket();
                        this.cancel();
                    }
                }
            }.runTaskTimer(DamageIndicator.INSTANCE, 0, 1);
            return this;
        }

        @Override
        public void sendCreatePacket() {
            armorStand = ent.getWorld().spawn(ent.getLocation(), ArmorStand.class);
        }

        @Override
        public void sendMetaPacket() {
            armorStand.setInvisible(true);
            try {
                armorStand.setMarker(true);
            } catch(Exception exc) {
                //Method does not exist pre-1.8

            }
            armorStand.setSmall(true);
            armorStand.setGravity(false);
            armorStand.setCustomName(text);
            armorStand.setCustomNameVisible(true);
        }

        @Override
        public void sendTeleportPacket() {
            armorStand.teleport(new Location(ent.getWorld(), ent.getEyeLocation().getX(), ent.getEyeLocation().getY() + dy, ent.getEyeLocation().getZ()));
        }

        @Override
        public void sendRemovePacket() {
            armorStand.remove();
        }
    }
}