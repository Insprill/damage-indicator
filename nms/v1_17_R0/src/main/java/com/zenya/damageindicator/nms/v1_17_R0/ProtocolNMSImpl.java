package com.zenya.damageindicator.nms.v1_17_R0;

import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
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

        public HologramImpl(LivingEntity entity, Location loc, String text) {
            this.entity = entity;

            WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
            this.armorStand = new EntityArmorStand(world, loc.getX(), loc.getY(), loc.getZ());
            this.armorStand.setInvisible(true);
            this.armorStand.setMarker(true);
            this.armorStand.setSmall(true);
            this.armorStand.setNoGravity(true);
            this.armorStand.setCustomName(new ChatComponentText(text));
            this.armorStand.setCustomNameVisible(true);
            this.tracker = world.getChunkProvider().a.G.get(entity.getEntityId());
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
            //PacketPlayOutEntityDestroy(int) in 1.17
            PacketPlayOutEntityDestroy remove = new PacketPlayOutEntityDestroy(armorStand.getId());
            sendPacketToWorld(remove);
        }

        @Override
        public void sendPacketToTracked(Object packet) {
            if (tracker == null) {
                sendPacketToWorld(packet);
                return;
            }
            for (ServerPlayerConnection conn : tracker.f) {
                sendPacketIfToggled(conn.d().getUniqueID(), conn, packet);
            }
        }

        @Override
        public void sendPacketToWorld(Object packet) {
            for (Player player : entity.getWorld().getPlayers()) {
                sendPacketIfToggled(player.getUniqueId(), ((CraftPlayer) player).getHandle().b, packet);
            }
        }

        @Override
        public void sendPacket(Object connection, Object packet) {
            ((ServerPlayerConnection) connection).sendPacket((Packet<?>) packet);
        }

    }

}
