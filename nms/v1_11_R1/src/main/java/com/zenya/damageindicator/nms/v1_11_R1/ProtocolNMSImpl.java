package com.zenya.damageindicator.nms.v1_11_R1;

import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import com.zenya.damageindicator.storage.StorageFileManager;
import net.minecraft.server.v1_11_R1.EntityArmorStand;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.EntityTrackerEntry;
import net.minecraft.server.v1_11_R1.Packet;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_11_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_11_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_11_R1.PlayerConnection;
import net.minecraft.server.v1_11_R1.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(LivingEntity ent, String text) {
        return new HologramImpl(ent, text);
    }

    public static class HologramImpl implements Hologram {

        private final EntityArmorStand armorStand;
        private final LivingEntity entity;
        private final EntityTrackerEntry tracker;

        public HologramImpl(LivingEntity entity, String text) {
            this.entity = entity;

            Location loc = entity.getLocation();
            this.armorStand = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
            this.armorStand.setInvisible(true);
            this.armorStand.setMarker(true);
            this.armorStand.setSmall(true);
            this.armorStand.setNoGravity(true);
            this.armorStand.setCustomName(text);
            this.armorStand.setCustomNameVisible(true);
            this.tracker = ((WorldServer) armorStand.world).tracker.trackedEntities.get(entity.getEntityId());
        }

        @Override
        public Hologram spawn(double offset, double speed, long duration) {
            sendCreatePacket();
            sendMetaPacket();
            new HologramRunnable(this, entity, offset, speed, duration).start();
            return this;
        }

        @Override
        public void sendCreatePacket() {
            PacketPlayOutSpawnEntityLiving create = new PacketPlayOutSpawnEntityLiving(armorStand);
            sendPacketToTracked(create);
        }

        @Override
        public void sendMetaPacket() {
            PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
            sendPacketToTracked(meta);
        }

        @Override
        public void sendTeleportPacket(Location loc) {
            armorStand.setPosition(loc.getX(), loc.getY(), loc.getZ());
            PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(armorStand);
            sendPacketToTracked(teleport);
        }

        @Override
        public void sendRemovePacket() {
            PacketPlayOutEntityDestroy remove = new PacketPlayOutEntityDestroy(armorStand.getId());
            sendPacketToWorld(remove);
        }

        @Override
        public void sendPacketToTracked(Object packet) {
            for (EntityPlayer conn : tracker.trackedPlayers) {
                sendPacketIfToggled(conn.getUniqueID(), conn.playerConnection, packet);
            }
        }

        @Override
        public void sendPacketToWorld(Object packet) {
            for (Player player : entity.getWorld().getPlayers()) {
                sendPacketIfToggled(player.getUniqueId(), ((CraftPlayer) player).getHandle().playerConnection, packet);
            }
        }

        @Override
        public void sendPacket(Object connection, Object packet) {
            ((PlayerConnection) connection).sendPacket((Packet<?>) packet);
        }

    }

}
