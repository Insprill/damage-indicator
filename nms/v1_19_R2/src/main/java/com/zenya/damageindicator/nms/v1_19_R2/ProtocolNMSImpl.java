package com.zenya.damageindicator.nms.v1_19_R2;

import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(LivingEntity ent, Location loc, String text) {
        return new HologramImpl(ent, loc, text);
    }

    public static class HologramImpl implements Hologram {

        private final ArmorStand armorStand;
        private final LivingEntity entity;
        private final ChunkMap.TrackedEntity tracker;

        public HologramImpl(LivingEntity entity, Location loc, String text) {
            this.entity = entity;

            ServerLevel world = ((CraftWorld) loc.getWorld()).getHandle();
            this.armorStand = new ArmorStand(world, loc.getX(), loc.getY(), loc.getZ());
            this.armorStand.setInvisible(true);
            this.armorStand.setMarker(true);
            this.armorStand.setSmall(true);
            this.armorStand.setNoGravity(true);
            this.armorStand.setCustomName(MutableComponent.create(new LiteralContents(text)));
            this.armorStand.setCustomNameVisible(true);
            this.tracker = world.getChunkSource().chunkMap.entityMap.get(entity.getEntityId());
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
            ClientboundAddEntityPacket create = new ClientboundAddEntityPacket(armorStand);
            sendPacketToTracked(create);
        }

        @Override
        public void sendMetaPacket() {
            List<SynchedEntityData.DataValue<?>> dataValues = armorStand.getEntityData().packDirty();
            if (dataValues == null)
                return;
            ClientboundSetEntityDataPacket meta = new ClientboundSetEntityDataPacket(armorStand.getId(), dataValues);
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
            if (tracker == null) {
                sendPacketToWorld(packet);
                return;
            }
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
