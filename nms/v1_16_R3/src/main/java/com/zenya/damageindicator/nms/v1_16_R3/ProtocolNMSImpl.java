package com.zenya.damageindicator.nms.v1_16_R3;

import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.PlayerChunkMap;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(LivingEntity ent, Location loc, String text) {
        return new HologramImpl(ent, loc, text);
    }

    public static class HologramImpl implements Hologram {

        private final EntityArmorStand armorStand;
        private final LivingEntity entity;
        private final PlayerChunkMap.EntityTracker tracker;
        private final WorldServer world;

        public HologramImpl(LivingEntity entity, Location loc, String text) {
            this.entity = entity;

            this.world = ((CraftWorld) loc.getWorld()).getHandle();
            this.armorStand = new EntityArmorStand(world, loc.getX(), loc.getY(), loc.getZ());
            this.armorStand.setInvisible(true);
            this.armorStand.setMarker(true);
            this.armorStand.setSmall(true);
            this.armorStand.setNoGravity(true);
            this.armorStand.setCustomName(new ChatComponentText(text));
            this.armorStand.setCustomNameVisible(true);
            this.tracker = world.getChunkProvider().playerChunkMap.trackedEntities.get(entity.getEntityId());
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
        public void sendPacketToTracked(Object packet) {
            for (EntityPlayer conn : tracker != null ? tracker.trackedPlayers : world.getPlayers()) {
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
