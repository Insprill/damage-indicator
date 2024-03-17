package com.zenya.damageindicator.nms.v1_8_R1;

import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import net.minecraft.server.v1_8_R1.EntityArmorStand;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EntityTrackerEntry;
import net.minecraft.server.v1_8_R1.Packet;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R1.PlayerConnection;
import net.minecraft.server.v1_8_R1.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(LivingEntity ent, Location loc, String text) {
        return new HologramImpl(ent, loc, text);
    }

    public static class HologramImpl implements Hologram {

        private final EntityArmorStand armorStand;
        private final LivingEntity entity;
        private final EntityTrackerEntry tracker;
        private final WorldServer world;

        public HologramImpl(LivingEntity entity, Location loc, String text) {
            this.entity = entity;

            this.world = ((CraftWorld) loc.getWorld()).getHandle();
            this.armorStand = new EntityArmorStand(world, loc.getX(), loc.getY(), loc.getZ());
            this.armorStand.setInvisible(true);
            this.armorStand.setSmall(true); // #setMarker
            this.armorStand.setGravity(false);
            this.armorStand.setCustomName(text);
            this.armorStand.setCustomNameVisible(true);
            this.tracker = (EntityTrackerEntry) world.tracker.trackedEntities.get(entity.getEntityId());
        }

        @Override
        public Hologram spawn(double offsetX, double offsetY, double offsetZ, double speed, long duration) {
            sendCreatePacket();
            sendMetaPacket();
            new HologramRunnable(this, entity, offsetX, offsetY, offsetZ, speed, duration).start();
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
        @SuppressWarnings("unchecked")
        public void sendPacketToTracked(Object packet) {
            for (EntityHuman conn : tracker != null ? (Set<EntityPlayer>) tracker.trackedPlayers : (List<EntityHuman>) world.players) {
                if (!(conn instanceof EntityPlayer)) continue;
                sendPacketIfToggled(conn.getUniqueID(), ((EntityPlayer) conn).playerConnection, packet);
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
            ((PlayerConnection) connection).sendPacket((Packet) packet);
        }

    }

}
