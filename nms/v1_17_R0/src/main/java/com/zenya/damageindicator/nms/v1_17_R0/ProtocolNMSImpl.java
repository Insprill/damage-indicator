package com.zenya.damageindicator.nms.v1_17_R0;

import com.zenya.damageindicator.DamageIndicator;
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
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ProtocolNMSImpl implements ProtocolNMS {

    @Override
    public Hologram getHologram(List<Player> players, LivingEntity ent, String text) {
        return new HologramImpl(ent, text);
    }

    public static class HologramImpl implements Hologram {

        private final EntityArmorStand armorStand;
        private final LivingEntity entity;
        private double dy;

        public HologramImpl(LivingEntity entity, String text) {
            this.entity = entity;
            this.dy = 0;
            Location loc = entity.getLocation();

            this.armorStand = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ());
            this.armorStand.setInvisible(true);
            this.armorStand.setMarker(true);
            this.armorStand.setSmall(true);
            this.armorStand.setNoGravity(true);
            this.armorStand.setCustomName(new ChatComponentText(text));
            this.armorStand.setCustomNameVisible(true);
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
                    armorStand.setPosition(entity.getEyeLocation().getX(), entity.getEyeLocation().getY() + dy, entity.getEyeLocation().getZ());
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
            //PacketPlayOutEntityDestroy(int) in 1.17
            PacketPlayOutEntityDestroy remove = new PacketPlayOutEntityDestroy(armorStand.getId());
            sendPacket(remove);
        }

        @Override
        public void sendPacket(Object packet) {
            PlayerChunkMap.EntityTracker tracker = ((WorldServer) armorStand.t).getChunkProvider().a.G.get(entity.getEntityId());
            if (tracker == null)
                return;
            tracker.broadcast((Packet<?>) packet);
        }

    }

}