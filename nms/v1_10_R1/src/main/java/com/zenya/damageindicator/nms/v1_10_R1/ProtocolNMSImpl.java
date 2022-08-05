package com.zenya.damageindicator.nms.v1_10_R1;

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import net.minecraft.server.v1_10_R1.EntityArmorStand;
import net.minecraft.server.v1_10_R1.Packet;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_10_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(List<Player> players, LivingEntity ent, String text) {
        return new HologramImpl(players, ent, text);
    }

    public static class HologramImpl implements Hologram {

        private final List<Player> players;
        private final EntityArmorStand armorStand;
        private final LivingEntity ent;
        private double dy;

        public HologramImpl(List<Player> players, LivingEntity ent, String text) {
            this.players = players;
            this.ent = ent;
            this.dy = 0;
            Location loc = ent.getLocation();

            EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            armorStand.setSmall(true);
            armorStand.setNoGravity(true);
            armorStand.setCustomName(text);
            armorStand.setCustomNameVisible(true);
            this.armorStand = armorStand;
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
                    armorStand.setPosition(ent.getEyeLocation().getX(), ent.getEyeLocation().getY() + dy, ent.getEyeLocation().getZ());
                    sendTeleportPacket();
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
            PacketPlayOutSpawnEntityLiving create = new PacketPlayOutSpawnEntityLiving(armorStand);
            sendPacket(create);
        }

        @Override
        public void sendMetaPacket() {
            PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
            sendPacket(meta);
        }

        @Override
        public void sendTeleportPacket() {
            PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(armorStand);
            sendPacket(teleport);
        }

        @Override
        public void sendRemovePacket() {
            PacketPlayOutEntityDestroy remove = new PacketPlayOutEntityDestroy(armorStand.getId());
            sendPacket(remove);
        }

        @Override
        public void sendPacket(Object packet) {
            for (Player player : players) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet<?>) packet);
            }
        }

    }

}
