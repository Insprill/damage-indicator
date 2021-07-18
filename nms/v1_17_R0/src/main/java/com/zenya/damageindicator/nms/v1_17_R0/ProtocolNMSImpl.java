package com.zenya.damageindicator.nms.v1_17_R0;

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.nms.Hologram;
import com.zenya.damageindicator.nms.ProtocolNMS;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ProtocolNMSImpl implements ProtocolNMS {
    @Override
    public Hologram getHologram(Player player, LivingEntity ent, String text) {
        return new HologramImpl(player, ent, text);
    }

    public class HologramImpl implements Hologram {
        EntityPlayer player;
        EntityArmorStand armorStand;
        LivingEntity ent;
        Location initialLoc;
        double dy;

        public HologramImpl(Player player, LivingEntity ent, String text) {
            this.player = ((CraftPlayer) player).getHandle();
            this.ent = ent;
            this.initialLoc = ent.getEyeLocation();
            this.dy = 0;

            EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) initialLoc.getWorld()).getHandle(), ent.getLocation().getX(), ent.getLocation().getY(), ent.getLocation().getZ());
            armorStand.setInvisible(true);
            armorStand.setMarker(true);
            armorStand.setSmall(true);
            armorStand.setNoGravity(true);
            armorStand.setCustomName(new ChatComponentText(text));
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
                    armorStand.setPosition(ent.getEyeLocation().getX(), ent.getEyeLocation().getY() + dy, ent.getEyeLocation().getZ());
                    sendTeleportPacket();
                    dy += speed;

                    tick++;
                    if(tick > duration) {
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
            player.b.sendPacket(create);
        }

        @Override
        public void sendMetaPacket() {
            PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(armorStand.getId(), armorStand.getDataWatcher(), true);
            player.b.sendPacket(meta);
        }

        @Override
        public void sendTeleportPacket() {
            PacketPlayOutEntityTeleport teleport = new PacketPlayOutEntityTeleport(armorStand);
            player.b.sendPacket(teleport);
        }

        @Override
        public void sendRemovePacket() {
            //PacketPlayOutEntityDestroy(int) in 1.17
            PacketPlayOutEntityDestroy remove = new PacketPlayOutEntityDestroy(armorStand.getId());
            player.b.sendPacket(remove);
        }
    }
}