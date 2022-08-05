package com.zenya.damageindicator.nms.v1_18_R1;

import com.zenya.damageindicator.DamageIndicator;
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
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(LivingEntity ent, String text) {
        return new HologramImpl(ent, text);
    }

    public static class HologramImpl implements Hologram {

        private final ArmorStand armorStand;
        private final LivingEntity entity;
        private final ChunkMap.TrackedEntity tracker;
        private double dy;

        public HologramImpl(LivingEntity entity, String text) {
            this.entity = entity;
            this.dy = 0;

            Location loc = entity.getLocation();
            this.armorStand = new ArmorStand(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
            this.armorStand.setInvisible(true);
            this.armorStand.setMarker(true);
            this.armorStand.setSmall(true);
            this.armorStand.setNoGravity(true);
            this.armorStand.setCustomName(new TextComponent(text));
            this.armorStand.setCustomNameVisible(true);
            this.tracker = ((ServerLevel) armorStand.level).chunkSource.chunkMap.entityMap.get(entity.getEntityId());
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
            ClientboundAddEntityPacket create = new ClientboundAddEntityPacket(armorStand);
            sendPacket(create);
        }

        @Override
        public void sendMetaPacket() {
            ClientboundSetEntityDataPacket meta = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData(), false);
            sendPacket(meta);
        }

        @Override
        public void sendTeleportPacket(Location loc) {
            armorStand.teleportTo(loc.getX(), loc.getY(), loc.getZ());
            ClientboundTeleportEntityPacket teleport = new ClientboundTeleportEntityPacket(armorStand);
            sendPacket(teleport);
        }

        @Override
        public void sendRemovePacket() {
            ClientboundRemoveEntitiesPacket remove = new ClientboundRemoveEntitiesPacket(armorStand.getId());
            sendPacket(remove);
        }

        @Override
        public void sendPacket(Object packet) {
            if (StorageFileManager.getConfig().getBool("show-self-holograms")) {
                tracker.broadcastAndSend((Packet<?>) packet);
            } else {
                tracker.broadcast((Packet<?>) packet);
            }
        }

    }

}
