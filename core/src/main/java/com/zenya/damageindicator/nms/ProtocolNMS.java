package com.zenya.damageindicator.nms;

import org.bukkit.entity.LivingEntity;

public interface ProtocolNMS {

    Hologram getHologram(LivingEntity ent, String text);

    class HologramImpl {
    }

}
