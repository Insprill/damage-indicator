package com.zenya.damageindicator.nms.v1_20_R1;

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(LivingEntity ent, Location loc, List<Player> players, String text) {
        return new HologramImpl(ent, loc, players, text);
    }

    public static class HologramImpl implements Hologram {

        private final ArmorStand armorStand;
        private final LivingEntity entity;
        private final List<Player> players;

        public HologramImpl(LivingEntity entity, Location loc, List<Player> players, String text) {
            this.entity = entity;
            this.players = players;

            ServerLevel world = ((CraftWorld) loc.getWorld()).getHandle();
            this.armorStand = new ArmorStand(world, loc.getX(), loc.getY(), loc.getZ());
            this.armorStand.setInvisible(true);
            this.armorStand.setMarker(true);
            this.armorStand.setSmall(true);
            this.armorStand.setNoGravity(true);
            this.armorStand.setCustomName(MutableComponent.create(new LiteralContents(text)));
            this.armorStand.setCustomNameVisible(true);
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
            ClientboundAddEntityPacket create = new ClientboundAddEntityPacket(
                    armorStand.getId(),
                    armorStand.getUUID(),
                    armorStand.getX(),
                    armorStand.getY(),
                    armorStand.getZ(),
                    armorStand.getXRot(),
                    armorStand.getYRot(),
                    armorStand.getType(),
                    0,
                    armorStand.getDeltaMovement(),
                    armorStand.getYHeadRot()
            );
            sendPacket(create);
        }

        @Override
        public void sendMetaPacket() {
            List<SynchedEntityData.DataValue<?>> dataValues = armorStand.getEntityData().packDirty();
            if (dataValues == null)
                return;
            ClientboundSetEntityDataPacket meta = new ClientboundSetEntityDataPacket(armorStand.getId(), dataValues);
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

        public void sendPacket(Packet<?> packet) {
            for (Player player : players) {
                ((CraftPlayer) player).getHandle().connection.send(packet);
            }
        }

    }

}
