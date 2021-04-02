package com.zenya.damageindicator.nms;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface ProtocolNMS {
    Hologram getHologram(Player player, LivingEntity ent, String text);
    class HologramImpl{};
}
