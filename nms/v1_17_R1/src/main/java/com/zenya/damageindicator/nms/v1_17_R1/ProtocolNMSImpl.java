package com.zenya.damageindicator.nms.v1_17_R1;

import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import com.zenya.damageindicator.storage.StorageFileManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(LivingEntity ent, String text) {
        return new HologramImpl(ent, text);
    }

    public static class HologramImpl implements Hologram {

        private final ArmorStand armorStand;
        private final LivingEntity entity;
        private final ChunkMap.TrackedEntity tracker;

        public HologramImpl(LivingEntity entity, String text) {
            this.entity = entity;

            Location loc = entity.getLocation();
            this.armorStand = new ArmorStand(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
            this.armorStand.setInvisible(true);
            this.armorStand.setMarker(true);
            this.armorStand.setSmall(true);
            this.armorStand.setNoGravity(true);
            this.armorStand.setCustomName(new TextComponent(text));
            this.armorStand.setCustomNameVisible(true);
            this.tracker = ((ServerLevel) armorStand.level).getChunkSource().chunkMap.entityMap.get(entity.getEntityId());
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
            ClientboundAddEntityPacket create = new ClientboundAddEntityPacket(armorStand);
            sendPacketToTracked(create);
        }

        @Override
        public void sendMetaPacket() {
            ClientboundSetEntityDataPacket meta = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData(), false);
            sendPacketToTracked(meta);
        }

        @Override
        public void sendTeleportPacket(Location loc) {
            armorStand.teleportTo(loc.getX(), loc.getY(), loc.getZ());
            ClientboundTeleportEntityPacket teleport = new ClientboundTeleportEntityPacket(armorStand);
            sendPacketToTracked(teleport);
        }

        @Override
        public void sendRemovePacket() {
            ClientboundRemoveEntitiesPacket remove = new ClientboundRemoveEntitiesPacket(armorStand.getId());
            sendPacketToWorld(remove);
        }

        @Override
        public void sendPacketToTracked(Object packet) {
            for (ServerPlayerConnection conn : tracker.seenBy) {
                sendPacketIfToggled(conn.getPlayer().getUUID(), conn, packet);
            }
        }

        @Override
        public void sendPacketToWorld(Object packet) {
            for (Player player : entity.getWorld().getPlayers()) {
                sendPacketIfToggled(player.getUniqueId(), ((CraftPlayer) player).getHandle().connection, packet);
            }
        }

        @Override
        public void sendPacket(Object connection, Object packet) {
            ((ServerPlayerConnection) connection).send((Packet<?>) packet);
        }

    }

}
