package com.zenya.damageindicator.nms.v1_17_R1;

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(List<Player> players, LivingEntity ent, String text) {
        return new HologramImpl(players, ent, text);
    }

    public static class HologramImpl implements Hologram {

        private final List<ServerPlayer> players;
        private final ArmorStand armorStand;
        private final LivingEntity ent;
        private double dy;

        public HologramImpl(List<Player> players, LivingEntity ent, String text) {
            this.players = players.stream().map(p -> ((CraftPlayer) p).getHandle()).collect(Collectors.toList());
            this.ent = ent;
            this.dy = 0;
            Location loc = ent.getLocation();

            ArmorStand armorStand = new ArmorStand(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            armorStand.setSmall(true);
            armorStand.setNoGravity(true);
            armorStand.setCustomName(new TextComponent(text));
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
                    armorStand.teleportTo(ent.getEyeLocation().getX(), ent.getEyeLocation().getY() + dy, ent.getEyeLocation().getZ());
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
            ClientboundAddEntityPacket create = new ClientboundAddEntityPacket(armorStand);
            sendPacket(create);
        }

        @Override
        public void sendMetaPacket() {
            ClientboundSetEntityDataPacket meta = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData(), false);
            sendPacket(meta);
        }

        @Override
        public void sendTeleportPacket() {
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
            for (ServerPlayer player : players) {
                player.connection.send((Packet<?>) packet);
            }
        }

    }

}
